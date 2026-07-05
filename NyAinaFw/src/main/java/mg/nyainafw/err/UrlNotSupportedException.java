package mg.nyainafw.err;

import java.util.Map;

import mg.nyainafw.mapping.UrlControllerMap;
import mg.nyainafw.mapping.UrlKey;

public class UrlNotSupportedException extends Exception {
    private final UrlKey urlGot;
    private final Map<UrlKey, UrlControllerMap> supportedUrl;

    public UrlNotSupportedException(UrlKey urlGot, Map<UrlKey, UrlControllerMap> supportedUrl) {
        super("URL non supportee : " + urlGot);
        this.urlGot = urlGot;
        this.supportedUrl = supportedUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("L'URL \"")
                .append(urlGot)
                .append("\" n'est pas supportee.\n\n");
        sb.append("URLs supportees :\n");

        for (UrlKey url : supportedUrl.keySet()) {
            sb.append(" - ")
                    .append(url)
                    .append(";\n");
        }
        return sb.toString();
    }
}
