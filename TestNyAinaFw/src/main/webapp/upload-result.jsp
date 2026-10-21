<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Resultat de l'upload</title>
</head>
<body>
    <h1><%= request.getAttribute("message") %></h1>
    <% if (request.getAttribute("storedName") != null) { %>
        <p>Nom enregistre : <%= request.getAttribute("storedName") %></p>
        <p>Taille : <%= request.getAttribute("size") %> octets</p>
    <% } %>
    <a href="${pageContext.request.contextPath}/upload">Retour</a>
</body>
</html>
