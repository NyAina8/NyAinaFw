package mg.nyainafw.servlet;

import java.lang.reflect.Proxy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import junit.framework.TestCase;
import mg.nyainafw.annotation.Authentification;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.Role;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.mapping.UrlHTTPMethod;
import mg.nyainafw.mapping.UrlKey;
import mg.nyainafw.mapping.UrlProcessor;

public class AuthenticationCheckerTest extends TestCase {
    @MyController
    public static class Controller {
        @UrlMapping("/public")
        public Object publicPage() {
            return null;
        }

        @Authentification
        @UrlMapping("/private")
        public Object privatePage() {
            return null;
        }

        @Authentification
        @Role("admin")
        @UrlMapping("/admin")
        public Object adminPage() {
            return null;
        }
    }

    public void testProtectedAndPublicRoutes() throws Exception {
        UrlProcessor processor = new UrlProcessor();
        processor.processControllerClass(Controller.class);
        AuthenticationChecker checker = new AuthenticationChecker();
        UrlKey publicUrl = new UrlKey("/public", UrlHTTPMethod.GET);
        UrlKey privateUrl = new UrlKey("/private", UrlHTTPMethod.GET);
        UrlKey adminUrl = new UrlKey("/admin", UrlHTTPMethod.GET);

        assertEquals(HttpServletResponse.SC_OK,
                checker.checkStatus(processor, publicUrl, request(null), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                checker.checkStatus(processor, privateUrl, request(null), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                checker.checkStatus(processor, privateUrl, request(session(null, null)), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_OK,
                checker.checkStatus(processor, privateUrl, request(session("Aina", null)), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
                checker.checkStatus(processor, adminUrl, request(null), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_FORBIDDEN,
                checker.checkStatus(processor, adminUrl, request(session("Aina", null)), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_FORBIDDEN,
                checker.checkStatus(processor, adminUrl, request(session("Aina", "membre")), "utilisateur", "role"));
        assertEquals(HttpServletResponse.SC_OK,
                checker.checkStatus(processor, adminUrl, request(session("Aina", "admin")), "utilisateur", "role"));
    }

    private HttpSession session(Object user, Object role) {
        return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(),
                new Class<?>[] { HttpSession.class }, (proxy, method, args) ->
                        "getAttribute".equals(method.getName())
                                ? ("utilisateur".equals(args[0]) ? user : "role".equals(args[0]) ? role : null)
                                : null);
    }

    private HttpServletRequest request(HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) ->
                        "getSession".equals(method.getName()) ? session : null);
    }
}
