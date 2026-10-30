package test.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.nyainafw.annotation.MyController;
import mg.nyainafw.annotation.Authentification;
import mg.nyainafw.annotation.Role;
import mg.nyainafw.annotation.UrlMapping;
import mg.nyainafw.mapping.UrlHTTPMethod;
import mg.nyainafw.model.ApiResponse;
import mg.nyainafw.model.ModelView;
import mg.nyainafw.model.MySession;
import test.model.Produit;

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

    @UrlMapping("/api/objet")
    public ApiResponse apiObjet(Produit produit) {
        return ApiResponse.ok(produit);
    }

    @UrlMapping("/objet")
    public ModelView objet(Produit produit) {
        ModelView modelView = new ModelView("/test");
        modelView.addObject("message",
                "Produit recu : id=" + produit.getId()
                        + ", nom=" + produit.getNom()
                        + ", prix=" + produit.getPrix()
                        + ", actif=" + produit.getActif());
        modelView.addObject("nomFramework", "NyAinaFw");
        return modelView;
    }

    @UrlMapping("/upload")
    public ModelView uploadForm() {
        return new ModelView("/upload");
    }

    @UrlMapping(value = "/upload", httpMethod = UrlHTTPMethod.POST)
    public ModelView upload(String titre, Part fichier, HttpServletRequest request) throws IOException {
        ModelView result = new ModelView("/upload-result");
        if (fichier == null || fichier.getSize() == 0 || fichier.getSubmittedFileName() == null
                || fichier.getSubmittedFileName().isBlank()) {
            result.addObject("message", "Choisissez un fichier avant d'envoyer le formulaire.");
            return result;
        }

        String configuredDirectory = request.getServletContext().getInitParameter("UPLOAD_DIR");
        Path directory = configuredDirectory == null || configuredDirectory.isBlank()
                ? Paths.get(System.getProperty("java.io.tmpdir"), "nyainafw-uploads")
                : Paths.get(configuredDirectory);
        Files.createDirectories(directory);

        String originalName = Paths.get(fichier.getSubmittedFileName().replace('\\', '/')).getFileName().toString();
        String extension = originalName.lastIndexOf('.') > 0
                ? originalName.substring(originalName.lastIndexOf('.')) : "";
        if (!extension.matches("\\.[A-Za-z0-9]{1,16}")) {
            extension = "";
        }
        String storedName = UUID.randomUUID().toString() + extension;
        try (InputStream input = fichier.getInputStream()) {
            Files.copy(input, directory.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }

        result.addObject("message", "Fichier enregistre.");
        result.addObject("titre", titre);
        result.addObject("storedName", storedName);
        result.addObject("size", fichier.getSize());
        return result;
    }

    @UrlMapping("/session")
    public ModelView sessionForm() {
        return new ModelView("/session");
    }
    @UrlMapping(value = "/session/connexion", httpMethod = UrlHTTPMethod.POST)
    public ModelView connexion(String utilisateur, String role, MySession session) {
        ModelView view = new ModelView("/session");
        if (utilisateur == null || utilisateur.isBlank()) {
            view.addObject("message", "Entrez un nom d'utilisateur.");
            return view;
        }

        session.setAttribute("utilisateur", utilisateur.trim());
        session.setAttribute("role", "admin".equals(role) ? "admin" : "membre");
        view.addObject("message", "Nom enregistre dans la session.");
        return view;
    }

    @UrlMapping("/session/profil")
    @Authentification
    public ApiResponse profil(MySession session) {
        Map<String, Object> data = new HashMap<>();
        data.put("utilisateur", session.getAttribute("utilisateur"));
        return ApiResponse.ok(data);
    }

    @UrlMapping("/session/admin")
    @Authentification
    @Role("admin")
    public ApiResponse admin(MySession session) {
        Map<String, Object> data = new HashMap<>();
        data.put("utilisateur", session.getAttribute("utilisateur"));
        data.put("role", session.getAttribute("role"));
        return ApiResponse.ok(data);
    }

    @UrlMapping(value = "/session/deconnexion", httpMethod = UrlHTTPMethod.POST)
    public ModelView deconnexion(MySession session) {
        session.destroy();
        ModelView view = new ModelView("/session");
        view.addObject("message", "Session fermee.");
        return view;
    }
}
