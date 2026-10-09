package mg.nyainafw.mapping;

import java.lang.reflect.Field;
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

            String parameterName = getParameterName(parameter);
            if (isSimpleType(parameterType)) {
                String rawValue = request == null ? null : request.getParameter(parameterName);
                args[i] = convertSimpleValue(rawValue, parameterType, parameterName);
            } else {
                args[i] = bindObject(parameterType, parameterName, request);
            }
        }

        return args;
    }

    private Object bindObject(Class<?> type, String parameterName, HttpServletRequest request) {
        try {
            Object instance = type.getDeclaredConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                if (!isSimpleType(field.getType())) {
                    continue;
                }

                String rawValue = getFieldValue(request, parameterName, field.getName());
                if (rawValue == null) {
                    continue;
                }

                field.setAccessible(true);
                field.set(instance, convertSimpleValue(rawValue, field.getType(), field.getName()));
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Impossible de creer l'objet " + type.getName()
                            + ". Verifier qu'il possede un constructeur sans argument.",
                    e);
        }
    }

    private String getFieldValue(HttpServletRequest request, String parameterName, String fieldName) {
        if (request == null) {
            return null;
        }

        String prefixedName = parameterName + "." + fieldName;
        String rawValue = request.getParameter(prefixedName);
        if (rawValue != null) {
            return rawValue;
        }
        return request.getParameter(fieldName);
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
