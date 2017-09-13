package com.example.ye.gametrade_in.Bean;

/**
 * Created by lykav on 9/13/2017.
 */

public class TradeGameBean {
    private int tradeGameId;
    private String trackingNumber;
    private Integer points;
    private Integer status;

    private GameBean game;


    private UserDetailBean sender;
    private AddressBean fromAddress;
    private Integer senderStatus = 0;

    private UserDetailBean receiver;
    private AddressBean toAddress;
    private Integer receiverStatus = 0;

    private AddressBean transferAddress;

    public int getTradeGameId() {
        return tradeGameId;
    }

    public void setTradeGameId(int tradeGameId) {
        this.tradeGameId = tradeGameId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public GameBean getGame() {
        return game;
    }

    public void setGame(GameBean game) {
        this.game = game;
    }

    public UserDetailBean getSender() {
        return sender;
    }

    public void setSender(UserDetailBean sender) {
        this.sender = sender;
    }

    public AddressBean getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(AddressBean fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Integer getSenderStatus() {
        return senderStatus;
    }

    public void setSenderStatus(Integer senderStatus) {
        this.senderStatus = senderStatus;
    }

    public UserDetailBean getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDetailBean receiver) {
        this.receiver = receiver;
    }

    public AddressBean getToAddress() {
        return toAddress;
    }

    public void setToAddress(AddressBean toAddress) {
        this.toAddress = toAddress;
    }

    public Integer getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(Integer receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public AddressBean getTransferAddress() {
        return transferAddress;
    }

    public void setTransferAddress(AddressBean transferAddress) {
        this.transferAddress = transferAddress;
    }
}
