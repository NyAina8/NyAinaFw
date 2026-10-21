<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Upload de fichier</title>
</head>
<body>
    <h1>Envoyer un fichier</h1>
    <form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data">
        <label for="titre">Titre</label>
        <input id="titre" name="titre" type="text">
        <label for="fichier">Fichier</label>
        <input id="fichier" name="fichier" type="file" required>
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>
