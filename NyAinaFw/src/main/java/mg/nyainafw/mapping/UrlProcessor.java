package mg.nyainafw.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.err.UrlAlreadyDefinedException;
import mg.nyainafw.err.UrlNotSupportedException;

public class UrlProcessor {
    private final List<Class<?>> controllerClasses = new ArrayList<>();
    private final Map<UrlKey, UrlControllerMap> urlMapps = new HashMap<>();

    public void processControllerClass(Class<?> clazz) throws UrlAlreadyDefinedException {
        if (!clazz.isAnnotationPresent(MyController.class)) {
            return;
        }

        controllerClasses.add(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(UrlMapping.class)) {
                UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
                UrlKey key = new UrlKey(urlMapping.value(), urlMapping.httpMethod());
                if (urlMapps.containsKey(key)) {
                    throw new UrlAlreadyDefinedException(key, urlMapps.get(key));
                }
                method.setAccessible(true);
                urlMapps.put(key, new UrlControllerMap(method, clazz));
            }
        }
    }

    public Object executeRequest(UrlKey url) throws UrlNotSupportedException, ReflectiveOperationException {
        if (!urlMapps.containsKey(url)) {
            throw new UrlNotSupportedException(url, urlMapps);
        }

        UrlControllerMap map = urlMapps.get(url);
        return map.getReflectMethod().invoke(map.getPrototypeSeed());
    }

    public List<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public Map<UrlKey, UrlControllerMap> getUrlMapps() {
        return urlMapps;
    }
}
