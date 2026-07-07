package mg.nyainafw.model;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String viewName;
    private Map<String, Object> data;

    public ModelView() {
        this.data = new HashMap<>();
    }

    public ModelView(String viewName) {
        this();
        this.viewName = viewName;
    }

    public ModelView(String viewName, Map<String, Object> data) {
        this.viewName = viewName;
        this.data = data == null ? new HashMap<>() : data;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data == null ? new HashMap<>() : data;
    }

    public void addObject(String key, Object value) {
        data.put(key, value);
    }
}
