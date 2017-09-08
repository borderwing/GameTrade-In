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
    <link rel="stylesheet" href="../../bootstrap/dist/css/bootstrap.min.css">
    <title>Order</title>
</head>
<body>
<h1>${username}'s order</h1>
<table class="table table-striped table-bordered table-hover">
    <tr>
        <th>TradeGameId</th>
        <th>Receiver</th>
        <th>ReceiveStatus</th>
        <th>Sender</th>
        <th>SenderStatus</th>
        <th>fromAddress</th>
        <th>toAddress</th>
        <th>gameId</th>
        <th>points</th>
    </tr>
    <c:forEach items="${TradeAsReceiver}" var="receive">
        <tr>
            <td>${receive.trade_gameid}</td>
            <td>${receive.receiver.username}</td>
            <td>${receive.receiver_status}</td>
            <td>${receive.sender.username}</td>
            <td>${receive.sender_status}</td>
            <td>${receive.from_addressid.address}</td>
            <td>${receive.to_addressid.address}</td>
            <td>${receive.game.gameId}</td>
            <td>${receive.points}</td>
        </tr>
    </c:forEach>
    <c:forEach items="${TradeAsSender}" var="send">
        <tr>
            <td>${send.trade_gameid}</td>
            <td>${send.receiver.username}</td>
            <td>${send.receiver_status}</td>
            <td>${send.sender.username}</td>
            <td>${send.sender_status}</td>
            <td>${send.from_addressid.address}</td>
            <td>${send.to_addressid.address}</td>
            <td>${send.game.gameId}</td>
            <td>${send.points}</td>
        </tr>
    </c:forEach>
</table>
<a type="button" href="/index/back" class="btn btn-sm btn-success">back</a>
</body>
</html>
