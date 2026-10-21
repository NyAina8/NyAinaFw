package mg.nyainafw.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import junit.framework.TestCase;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;

public class UrlProcessorUploadTest extends TestCase {
    @MyController
    public static class UploadController {
        @UrlMapping(value = "/upload-test", httpMethod = UrlHTTPMethod.POST)
        public String upload(String titre, Part fichier, int nombre) {
            return titre + ":" + fichier.getName() + ":" + nombre;
        }
    }

    public void testMultipartPartAndTextParameters() throws Exception {
        final Map<String, String> values = new HashMap<>();
        values.put("titre", "Document");
        values.put("nombre", "3");

        final Part part = (Part) Proxy.newProxyInstance(Part.class.getClassLoader(),
                new Class<?>[] { Part.class }, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        return "getName".equals(method.getName()) ? "fichier" : null;
                    }
                });
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(), new Class<?>[] { HttpServletRequest.class },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getParameter".equals(method.getName())) {
                            return values.get(args[0]);
                        }
                        if ("getPart".equals(method.getName())) {
                            return "fichier".equals(args[0]) ? part : null;
                        }
                        return null;
                    }
                });

        UrlProcessor processor = new UrlProcessor();
        processor.processControllerClass(UploadController.class);
        assertEquals("Document:fichier:3",
                processor.executeRequest(new UrlKey("/upload-test", UrlHTTPMethod.POST), request));
    }
}
