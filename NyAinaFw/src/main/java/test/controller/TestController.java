package test.controller;

import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.model.ApiResponse;
import mg.nyainafw.model.ModelView;

import java.util.Map;

@MyController
public class TestController {

    @UrlMapping("/test")
    public ModelView test() {
        ModelView modelView = new ModelView("/test");
        modelView.addObject("message", "Bonjour depuis une cle JSP");
        modelView.addObject("nomFramework", "NyAinaFw");
        return modelView;
    }

    @UrlMapping("/api/test")
    public ApiResponse apiTest() {
        return ApiResponse.ok(Map.of(
                "message", "Bonjour depuis l'API",
                "nomFramework", "NyAinaFw"));
    }
}
