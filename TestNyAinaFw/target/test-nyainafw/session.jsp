<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Test de session</title>
</head>
<body>
    <h1>Session utilisateur</h1>
    <% if (request.getAttribute("message") != null) { %>
        <p><%= request.getAttribute("message") %></p>
    <% } %>
    <form action="${pageContext.request.contextPath}/session/connexion" method="post">
        <label for="utilisateur">Nom d'utilisateur</label>
        <input id="utilisateur" name="utilisateur" type="text" required>
        <label for="role">Role</label>
        <select id="role" name="role">
            <option value="membre">Membre</option>
            <option value="admin">Admin</option>
        </select>
        <button type="submit">Enregistrer</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/session/profil">Voir le profil</a></p>
    <p><a href="${pageContext.request.contextPath}/session/admin">Page admin</a></p>
    <form action="${pageContext.request.contextPath}/session/deconnexion" method="post">
        <button type="submit">Se deconnecter</button>
    </form>
</body>
</html>
