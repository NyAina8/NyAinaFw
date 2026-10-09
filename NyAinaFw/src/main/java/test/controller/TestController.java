package test.controller;

import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.model.ApiResponse;
import mg.nyainafw.model.ModelView;

import java.util.HashMap;
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
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Bonjour depuis l'API");
        data.put("nomFramework", "NyAinaFw");
        return ApiResponse.ok(data);
    }

    @UrlMapping("/api/primitive")
    public ApiResponse apiPrimitive(
            int id,
            double prix,
            boolean actif,
            String nom) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("prix", prix);
        data.put("actif", actif);
        data.put("nom", nom);
        return ApiResponse.ok(data);
    }
}
