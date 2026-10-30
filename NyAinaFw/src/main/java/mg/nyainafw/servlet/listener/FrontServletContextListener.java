package mg.nyainafw.servlet.listener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.mapping.UrlProcessor;
import mg.nyainafw.model.MySession;
import mg.nyainafw.util.ClasspathScanner;

@WebListener
public class FrontServletContextListener implements ServletContextListener {
    public static final String URL_PROCESSOR_ATTR = "urlProcessor";
    public static final String SESSIONS_ATTR = "sessions";
    public static final String CONTROLLER_PACKAGE = "CONTROLLER_PACKAGE";
    public static final String USER_SESSION_KEY_PARAM = "auth.user-session-key";
    public static final String USER_SESSION_KEY_ATTR = "userSessionKey";
    public static final String ROLE_SESSION_KEY_PARAM = "auth.role-session-key";
    public static final String ROLE_SESSION_KEY_ATTR = "roleSessionKey";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String userSessionKey = context.getInitParameter(USER_SESSION_KEY_PARAM);
        if (userSessionKey == null || userSessionKey.isBlank()) {
            throw new IllegalStateException("Le parametre auth.user-session-key est obligatoire");
        }
        context.setAttribute(USER_SESSION_KEY_ATTR, userSessionKey);
        String roleSessionKey = context.getInitParameter(ROLE_SESSION_KEY_PARAM);
        if (roleSessionKey == null || roleSessionKey.isBlank()) {
            throw new IllegalStateException("Le parametre auth.role-session-key est obligatoire");
        }
        context.setAttribute(ROLE_SESSION_KEY_ATTR, roleSessionKey);
        String controllerPackage = context.getInitParameter(CONTROLLER_PACKAGE);
        if (controllerPackage == null) {
            controllerPackage = "";
        }

        UrlProcessor urlProcessor = new UrlProcessor();
        try {
            List<Class<?>> controllers = ClasspathScanner.getClassesAnnotatedWith(MyController.class, controllerPackage);
            for (Class<?> controller : controllers) {
                urlProcessor.processControllerClass(controller);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation du UrlProcessor", e);
        }

        context.setAttribute(URL_PROCESSOR_ATTR, urlProcessor);
        context.setAttribute(SESSIONS_ATTR, new ConcurrentHashMap<String, MySession>());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
