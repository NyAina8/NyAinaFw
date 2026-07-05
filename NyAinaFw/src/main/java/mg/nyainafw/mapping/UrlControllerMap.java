package mg.nyainafw.mapping;

import java.lang.reflect.Method;

public class UrlControllerMap {
    private Method reflectMethod;
    private Class<?> controllerClass;

    public UrlControllerMap(Method reflectMethod, Class<?> controllerClass) {
        this.reflectMethod = reflectMethod;
        this.controllerClass = controllerClass;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Object getPrototypeSeed() throws ReflectiveOperationException {
        return controllerClass.getConstructor().newInstance();
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public Method getReflectMethod() {
        return reflectMethod;
    }

    public void setReflectMethod(Method reflectMethod) {
        this.reflectMethod = reflectMethod;
    }

    @Override
    public String toString() {
        return "UrlControllerMap [method=" + reflectMethod + ", controllerClass=" + controllerClass + "]";
    }
}
