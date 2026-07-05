package mg.nyainafw.servlet.listener;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.mapping.UrlProcessor;
import mg.nyainafw.util.ClasspathScanner;

@WebListener
public class FrontServletContextListener implements ServletContextListener {
    public static final String URL_PROCESSOR_ATTR = "urlProcessor";
    public static final String CONTROLLER_PACKAGE = "CONTROLLER_PACKAGE";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
