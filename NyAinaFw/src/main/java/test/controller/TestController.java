package test.controller;

import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.model.ModelView;

@MyController
public class TestController {

    @UrlMapping("/test")
    public ModelView test() {
        ModelView modelView = new ModelView("/test");
        modelView.addObject("message", "Bonjour depuis une cle JSP");
        modelView.addObject("nomFramework", "NyAinaFw");
        return modelView;
    }
}
