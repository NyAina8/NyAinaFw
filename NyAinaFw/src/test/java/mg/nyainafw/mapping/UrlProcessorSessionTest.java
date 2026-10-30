package mg.nyainafw.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import junit.framework.TestCase;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;

public class UrlProcessorSessionTest extends TestCase {
    @MyController
    public static class SessionController {
        @UrlMapping("/session-test")
        public Object lire(HttpSession session) {
            return session.getAttribute("utilisateur");
        }
    }

    public void testSessionIsInjectedIntoController() throws Exception {
        final HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(), new Class<?>[] { HttpSession.class },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        return "getAttribute".equals(method.getName()) ? "Aina" : null;
                    }
                });
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(), new Class<?>[] { HttpServletRequest.class },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        return "getSession".equals(method.getName()) ? session : null;
                    }
                });

        UrlProcessor processor = new UrlProcessor();
        processor.processControllerClass(SessionController.class);
        assertEquals("Aina", processor.executeRequest(new UrlKey("/session-test", UrlHTTPMethod.GET), request));
    }
}
