package mg.nyainafw.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.err.UrlAlreadyDefinedException;
import mg.nyainafw.err.UrlNotSupportedException;
import jakarta.servlet.http.HttpServletRequest;

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
        return executeRequest(url, null);
    }

    public Object executeRequest(UrlKey url, HttpServletRequest request)
            throws UrlNotSupportedException, ReflectiveOperationException {
        if (!urlMapps.containsKey(url)) {
            throw new UrlNotSupportedException(url, urlMapps);
        }

        UrlControllerMap map = urlMapps.get(url);
        Object[] args = resolveMethodArguments(map.getReflectMethod(), request);
        return map.getReflectMethod().invoke(map.getPrototypeSeed(), args);
    }

    private Object[] resolveMethodArguments(Method method, HttpServletRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();

            if (parameterType.equals(HttpServletRequest.class)) {
                args[i] = request;
                continue;
            }

            if (!isSimpleType(parameterType)) {
                throw new IllegalArgumentException(
                        "Type de parametre non supporte pour le moment: " + parameterType.getName());
            }

            String parameterName = getParameterName(parameter);
            String rawValue = request == null ? null : request.getParameter(parameterName);
            args[i] = convertSimpleValue(rawValue, parameterType, parameterName);
        }

        return args;
    }

    private String getParameterName(Parameter parameter) {
        return parameter.getName();
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || type.equals(Integer.class)
                || type.equals(Double.class)
                || type.equals(Boolean.class)
                || type.equals(Long.class)
                || type.equals(Float.class)
                || type.equals(Short.class)
                || type.equals(Byte.class)
                || type.equals(Character.class);
    }

    private Object convertSimpleValue(String value, Class<?> type, String parameterName) {
        if (value == null || value.isEmpty()) {
            if (type.isPrimitive()) {
                throw new IllegalArgumentException("Parametre obligatoire manquant: " + parameterName);
            }
            return null;
        }

        if (type == String.class) {
            return value;
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        }
        if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        }
        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        }

        throw new IllegalArgumentException("Type de conversion non supporte: " + type.getName());
    }

    public List<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public Map<UrlKey, UrlControllerMap> getUrlMapps() {
        return urlMapps;
    }
}
