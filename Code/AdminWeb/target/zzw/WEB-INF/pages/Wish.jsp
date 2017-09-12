<%--
  Created by IntelliJ IDEA.
  User: homepppp
  Date: 2017/9/7
  Time: 23:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>WishGames</title>
    <link rel="stylesheet" href="../../bootstrap/dist/css/bootstrap.min.css">
</head>
<body>
<h1>${username}'s WishGames</h1>
<table class="table table-striped table-bordered table-hover">
    <tr>
        <th>WishGameId</th>
        <th>createTime</th>
        <th>Points</th>
        <th>Operation</th>
    </tr>
    <c:forEach items="${wishList}" var="wish">
        <tr>
            <td>${wish.wishEntityPK.game.gameId}</td>
            <td>${wish.wishEntityPK.create_time}</td>
            <td>${wish.points}</td>
            <td><a type="button" href="/index/${userId}/wishGames/delete/${wish.game.gameId}" class="btn btn-sm btn-success">delete</a></td>
        </tr>
    </c:forEach>
</table>
<a type="button" href="/index/back" class="btn btn-sm btn-success">back</a>
</body>
</html>
