<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1,minimum-scale-1,user-scalable=no" >
    <link rel="stylesheet" href="../../bootstrap/dist/css/bootstrap.min.css">
    <script src="../jquery/jquery.min.js"></script>
    <title>TradeIn Admin</title>
</head>
<body>
<h1>Admin Index</h1>
<div id="container">
        <table class="table table-striped table-bordered table-hover">
            <tr>
                <th>UserId</th>
                <th>UserName</th>
                <th>OfferGames</th>
                <th>WishGames</th>
                <th>Orders</th>
            </tr>
            <c:forEach items="${userList}" var="user">
                <tr>
                    <td>${user.userId}</td>
                    <td>${user.username}</td>
                    <td><a href="/index/offerGames/${user.userId}" type="button" class="btn btn-sm btn-success">OfferGame</a></td>
                    <td><a href="/index/wishGames/${user.userId}" type="button" class="btn btn-sm btn-success">WishGame</a></td>
                    <td><a href="/index/orders/${user.userId}" type="button" class="btn btn-sm btn-success">Order</a></td>
                </tr>
            </c:forEach>
        </table>
</div>
</body>
</html>