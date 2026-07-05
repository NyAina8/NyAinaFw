package mg.nyainafw.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.nyainafw.err.UrlNotSupportedException;
import mg.nyainafw.mapping.UrlHTTPMethod;
import mg.nyainafw.mapping.UrlKey;
import mg.nyainafw.mapping.UrlProcessor;
import mg.nyainafw.servlet.listener.FrontServletContextListener;

public class FrontController extends HttpServlet {
    private UrlProcessor urlProcessor;

    @Override
    public void init() throws ServletException {
        urlProcessor = (UrlProcessor) getServletContext()
                .getAttribute(FrontServletContextListener.URL_PROCESSOR_ATTR);
        if (urlProcessor == null) {
            throw new ServletException("UrlProcessor introuvable dans le ServletContext");
        }
    }

    private Object executeRequest(HttpServletRequest request)
            throws UrlNotSupportedException, ReflectiveOperationException {
        String url = getRequestedUrl(request);
        UrlHTTPMethod method = UrlHTTPMethod.buildUrlHTTPMethod(request.getMethod());
        return urlProcessor.executeRequest(new UrlKey(url, method));
    }

    private String getRequestedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        return uri.substring(context.length());
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            Object result = executeRequest(request);
            printDebugPage(request, out, result);
        } catch (UrlNotSupportedException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            printError(out, e.toString());
        } catch (ReflectiveOperationException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printError(out, e.getMessage());
            e.printStackTrace();
        }
    }

    private void printDebugPage(HttpServletRequest request, PrintWriter out, Object result) {
        out.println("<html><body>");
        out.println("<h1>Bienvenue dans NyAina-FrameWork</h1>");
        out.println("<p>URL demandee : " + request.getRequestURL() + "</p>");
        if (result != null) {
            out.println("<h2>Resultat :</h2>");
            out.println("<p>" + result + "</p>");
        }
        printControllers(out);
        printMappings(out);
        out.println("</body></html>");
    }

    private void printControllers(PrintWriter out) {
        out.println("<h2>Liste des controllers :</h2>");
        urlProcessor.getControllerClasses()
                .forEach(controller -> out.println("<p>" + controller.getName() + "</p>"));
    }

    private void printMappings(PrintWriter out) {
        out.println("<h2>Liste des URLs :</h2>");
        urlProcessor.getUrlMapps()
                .forEach((key, value) -> out.println("<p>" + key + " : " + value + "</p>"));
    }

    private void printError(PrintWriter out, String message) {
        out.println("<html><body>");
        out.println("<pre>" + message + "</pre>");
        out.println("</body></html>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
