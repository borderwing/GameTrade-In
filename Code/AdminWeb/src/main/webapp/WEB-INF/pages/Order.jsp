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
        <th>Status</th>
        <th>points</th>
        <th>operation</th>
    </tr>
    <c:forEach items="${TradeAsReceiver}" var="receive">
        <tr>
            <td>${receive.trade_gameid}</td>
            <td>${receive.receiver.username}</td>
            <td><c:if test="${receive.receiver_status==2}">reject</c:if><c:if test="${receive.receiver_status==1}">unconfirmed</c:if><c:if test="${receive.receiver_status==0}">confirmed</c:if> </td>
            <td>${receive.sender.username}</td>
            <td><c:if test="${receive.sender_status==2}">reject</c:if><c:if test="${receive.sender_status==1}">unconfirmed</c:if><c:if test="${receive.sender_status==0}">confirmed</c:if></td>
            <td>${receive.from_addressid.address}</td>
            <td>${receive.to_addressid.address}</td>
            <td>${receive.game.gameId}</td>
            <td><c:if test="${receive.status==0}">confirmed</c:if><c:if test="${receive.status==-1}">canceled</c:if><c:if test="${receive.status!=0 and receive.status!=-1}">unconfirmed</c:if></td>
            <td>${receive.points}</td>
            <td><a type="button" href="/index/orders/delete/${userId}/${receive.trade_gameid}">delete</a></td>
        </tr>
    </c:forEach>
    <c:forEach items="${TradeAsSender}" var="send">
        <tr>
            <td>${send.trade_gameid}</td>
            <td>${send.receiver.username}</td>
            <td><c:if test="${send.receiver_status==2}">reject</c:if><c:if test="${send.receiver_status==1}">unconfirmed</c:if><c:if test="${send.receiver_status==0}">confirmed</c:if></td>
            <td>${send.sender.username}</td>
            <td><c:if test="${send.sender_status==2}">reject</c:if><c:if test="${send.sender_status==1}">unconfirmed</c:if><c:if test="${send.sender_status==0}">confirmed</c:if></td>
            <td>${send.from_addressid.address}</td>
            <td>${send.to_addressid.address}</td>
            <td>${send.game.gameId}</td>
            <td><c:if test="${send.status==0}">confirmed</c:if><c:if test="${send.status==-1}">canceled</c:if><c:if test="${send.status!=0 and send.status!=-1}">unconfirmed</c:if></td>
            <td>${send.points}</td>
            <td><a type="button" href="/index/orders/delete/${userId}/${send.trade_gameid}">delete</a></td>
        </tr>
    </c:forEach>
</table>
<a type="button" href="/index/back" class="btn btn-sm btn-success">back</a>
</body>
</html>
