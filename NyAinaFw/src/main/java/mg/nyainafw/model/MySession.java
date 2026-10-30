package mg.nyainafw.model;

import java.util.HashMap;
import java.util.Map;

public class MySession {
    private final Map<String, Object> sessionData = new HashMap<>();
    private boolean destroyed;

    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    public Object getAttribute(String key) {
        return sessionData.get(key);
    }

    public void setAttribute(String key, Object value) {
        sessionData.put(key, value);
    }

    public void removeAttribute(String key) {
        sessionData.remove(key);
    }

    public void destroy() {
        sessionData.clear();
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
