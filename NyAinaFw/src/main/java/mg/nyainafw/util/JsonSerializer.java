package mg.nyainafw.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

public final class JsonSerializer {
    private JsonSerializer() {
    }

    public static String toJson(Object value) {
        return toJson(value, new IdentityHashMap<>());
    }

    private static String toJson(Object value, Map<Object, Boolean> visited) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            String text = (String) value;
            return quote(text);
        }
        if (value instanceof Character) {
            Character character = (Character) value;
            return quote(character.toString());
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Enum<?>) {
            Enum<?> enumValue = (Enum<?>) value;
            return quote(enumValue.name());
        }
        if (visited.containsKey(value)) {
            throw new IllegalArgumentException("Cycle detecte pendant la serialisation JSON");
        }

        visited.put(value, Boolean.TRUE);
        try {
            if (value instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) value;
                return serializeMap(map, visited);
            }
            if (value instanceof Iterable<?>) {
                Iterable<?> iterable = (Iterable<?>) value;
                return serializeIterable(iterable, visited);
            }
            if (value.getClass().isArray()) {
                return serializeArray(value, visited);
            }
            return serializeBean(value, visited);
        } finally {
            visited.remove(value);
        }
    }

    private static String serializeMap(Map<?, ?> map, Map<Object, Boolean> visited) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(quote(String.valueOf(entry.getKey())));
            json.append(':');
            json.append(toJson(entry.getValue(), visited));
        }
        return json.append('}').toString();
    }

    private static String serializeIterable(Iterable<?> iterable, Map<Object, Boolean> visited) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object item : iterable) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(toJson(item, visited));
        }
        return json.append(']').toString();
    }

    private static String serializeArray(Object array, Map<Object, Boolean> visited) {
        StringBuilder json = new StringBuilder("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(toJson(Array.get(array, i), visited));
        }
        return json.append(']').toString();
    }

    private static String serializeBean(Object bean, Map<Object, Boolean> visited) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Method method : bean.getClass().getMethods()) {
            if (!isGetter(method)) {
                continue;
            }
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(quote(propertyName(method)));
            json.append(':');
            try {
                json.append(toJson(method.invoke(bean), visited));
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException("Impossible de serialiser " + method.getName(), e);
            }
        }
        return json.append('}').toString();
    }

    private static boolean isGetter(Method method) {
        if (!Modifier.isPublic(method.getModifiers()) || method.getParameterCount() != 0) {
            return false;
        }
        if (method.getDeclaringClass() == Object.class) {
            return false;
        }
        String name = method.getName();
        return name.startsWith("get") && name.length() > 3 && method.getReturnType() != Void.TYPE;
    }

    private static String propertyName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static String quote(String text) {
        StringBuilder quoted = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    quoted.append("\\\"");
                    break;
                case '\\':
                    quoted.append("\\\\");
                    break;
                case '\b':
                    quoted.append("\\b");
                    break;
                case '\f':
                    quoted.append("\\f");
                    break;
                case '\n':
                    quoted.append("\\n");
                    break;
                case '\r':
                    quoted.append("\\r");
                    break;
                case '\t':
                    quoted.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        quoted.append(String.format("\\u%04x", (int) c));
                    } else {
                        quoted.append(c);
                    }
                    break;
            }
        }
        return quoted.append('"').toString();
    }
}
