package mg.nyainafw.servlet;

import java.lang.reflect.Method;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.nyainafw.annotation.Authentification;
import mg.nyainafw.annotation.Role;
import mg.nyainafw.mapping.UrlKey;
import mg.nyainafw.mapping.UrlProcessor;

public class AuthenticationChecker {
    public int checkStatus(UrlProcessor processor, UrlKey url, HttpServletRequest request,
            String userSessionKey, String roleSessionKey) {
        if (!processor.getUrlMapps().containsKey(url)) {
            return HttpServletResponse.SC_OK;
        }
        Method method = processor.getUrlMapps().get(url).getReflectMethod();
        Role roleAnnotation = method.getAnnotation(Role.class);
        if (!method.isAnnotationPresent(Authentification.class) && roleAnnotation == null) {
            return HttpServletResponse.SC_OK;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(userSessionKey) == null) {
            return HttpServletResponse.SC_UNAUTHORIZED;
        }
        if (roleAnnotation != null) {
            Object role = session.getAttribute(roleSessionKey);
            if (role == null || !Arrays.asList(roleAnnotation.value()).contains(role.toString())) {
                return HttpServletResponse.SC_FORBIDDEN;
            }
        }
        return HttpServletResponse.SC_OK;
    }
}
