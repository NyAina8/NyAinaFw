package mg.nyainafw.mapping;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import junit.framework.TestCase;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.model.MySession;
import mg.nyainafw.servlet.listener.FrontServletContextListener;

public class UrlProcessorMySessionTest extends TestCase {
    @MyController
    public static class Controller {
        @UrlMapping("/write")
        public Object write(MySession session) {
            session.setAttribute("name", "Aina");
            return session.getAttribute("name");
        }

        @UrlMapping("/read")
        public Object read(MySession session) {
            return session.getAttribute("name");
        }

        @UrlMapping("/remove")
        public Object remove(MySession session) {
            session.removeAttribute("name");
            return null;
        }

        @UrlMapping("/destroy")
        public Object destroy(MySession session) {
            session.destroy();
            return null;
        }
    }

    public void testReadWriteRemoveAndIsolation() throws Exception {
        Map<String, MySession> registry = new ConcurrentHashMap<>();
        ServletContext context = context(registry);
        Map<String, Object> firstData = new HashMap<>();
        Map<String, Object> secondData = new HashMap<>();
        HttpServletRequest first = request(context, session("one", firstData));
        HttpServletRequest second = request(context, session("two", secondData));
        UrlProcessor processor = new UrlProcessor();
        processor.processControllerClass(Controller.class);

        assertEquals("Aina", call(processor, "/write", first));
        assertEquals("Aina", call(processor, "/read", first));
        assertNull(call(processor, "/read", second));
        assertEquals(2, registry.size());
        call(processor, "/remove", first);
        assertNull(call(processor, "/read", first));
        assertFalse(firstData.containsKey("name"));
        call(processor, "/destroy", first);
        assertFalse(registry.containsKey("one"));
        assertTrue(registry.containsKey("two"));
    }

    private Object call(UrlProcessor processor, String path, HttpServletRequest request) throws Exception {
        return processor.executeRequest(new UrlKey(path, UrlHTTPMethod.GET), request);
    }

    private ServletContext context(Map<String, MySession> registry) {
        return (ServletContext) Proxy.newProxyInstance(ServletContext.class.getClassLoader(),
                new Class<?>[] { ServletContext.class }, (proxy, method, args) ->
                        "getAttribute".equals(method.getName())
                                && FrontServletContextListener.SESSIONS_ATTR.equals(args[0]) ? registry : null);
    }

    private HttpSession session(String id, Map<String, Object> data) {
        return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(),
                new Class<?>[] { HttpSession.class }, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getId": return id;
                        case "getAttributeNames": return Collections.enumeration(data.keySet());
                        case "getAttribute": return data.get(args[0]);
                        case "setAttribute": data.put((String) args[0], args[1]); return null;
                        case "removeAttribute": data.remove(args[0]); return null;
                        case "invalidate": data.clear(); return null;
                        default: return null;
                    }
                });
    }

    private HttpServletRequest request(ServletContext context, HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) -> {
                    if ("getSession".equals(method.getName())) return session;
                    if ("getServletContext".equals(method.getName())) return context;
                    return null;
                });
    }
}
