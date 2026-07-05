package mg.nyainafw.mapping;

public enum UrlHTTPMethod {
    GET,
    POST;

    public static UrlHTTPMethod buildUrlHTTPMethod(String method) {
        if ("POST".equalsIgnoreCase(method)) {
            return POST;
        }
        return GET;
    }
}
