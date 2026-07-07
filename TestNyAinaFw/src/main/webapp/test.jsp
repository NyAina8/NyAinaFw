<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test NyAinaFw</title>
</head>
<body>
    <h1><%= request.getAttribute("message") %></h1>
    <p>Framework : <%= request.getAttribute("nomFramework") %></p>
</body>
</html>
