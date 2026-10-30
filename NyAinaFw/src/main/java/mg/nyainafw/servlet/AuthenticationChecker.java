package mg.nyainafw.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.nyainafw.annotation.Authentification;
import mg.nyainafw.mapping.UrlKey;
import mg.nyainafw.mapping.UrlProcessor;

public class AuthenticationChecker {
    public boolean isAuthorized(UrlProcessor processor, UrlKey url, HttpServletRequest request, String userSessionKey) {
        if (!processor.getUrlMapps().containsKey(url)
                || !processor.getUrlMapps().get(url).getReflectMethod().isAnnotationPresent(Authentification.class)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(userSessionKey) != null;
    }
}
