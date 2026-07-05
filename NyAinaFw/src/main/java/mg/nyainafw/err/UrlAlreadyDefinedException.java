package mg.nyainafw.err;

import mg.nyainafw.mapping.UrlControllerMap;
import mg.nyainafw.mapping.UrlKey;

public class UrlAlreadyDefinedException extends Exception {
    public UrlAlreadyDefinedException(UrlKey url, UrlControllerMap value) {
        super("Url " + url + " est deja definie pour la valeur " + value);
    }
}
