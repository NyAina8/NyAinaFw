package mg.nyainafw.servlet;

import java.lang.reflect.Proxy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import junit.framework.TestCase;
import mg.nyainafw.annotation.Authentification;
import mg.nyainafw.annotation.MyController;
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
    }

    public void testProtectedAndPublicRoutes() throws Exception {
        UrlProcessor processor = new UrlProcessor();
        processor.processControllerClass(Controller.class);
        AuthenticationChecker checker = new AuthenticationChecker();
        UrlKey publicUrl = new UrlKey("/public", UrlHTTPMethod.GET);
        UrlKey privateUrl = new UrlKey("/private", UrlHTTPMethod.GET);

        assertTrue(checker.isAuthorized(processor, publicUrl, request(null), "utilisateur"));
        assertFalse(checker.isAuthorized(processor, privateUrl, request(null), "utilisateur"));
        assertFalse(checker.isAuthorized(processor, privateUrl, request(session(null)), "utilisateur"));
        assertTrue(checker.isAuthorized(processor, privateUrl, request(session("Aina")), "utilisateur"));
    }

    private HttpSession session(Object user) {
        return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(),
                new Class<?>[] { HttpSession.class }, (proxy, method, args) ->
                        "getAttribute".equals(method.getName()) && "utilisateur".equals(args[0]) ? user : null);
    }

    private HttpServletRequest request(HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) ->
                        "getSession".equals(method.getName()) ? session : null);
    }
}
