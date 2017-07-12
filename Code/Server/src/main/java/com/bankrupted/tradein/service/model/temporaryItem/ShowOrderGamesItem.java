package com.bankrupted.tradein.service.model.temporaryItem;

import com.bankrupted.tradein.service.model.AddressEntity;
import com.bankrupted.tradein.service.model.UserEntity;

/**
 * Created by homepppp on 2017/7/5.
 */
public class ShowOrderGamesItem {
    private int tradeGameId;

    private UserEntity sender;

    private UserEntity receiver;

    private int senderStatus;
    private int receiverStatus;
    private int status;
    private int gameId;

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public AddressEntity getToAddress() {
        return toAddress;
    }

    public void setToAddress(AddressEntity toAddress) {
        this.toAddress = toAddress;
    }

    public AddressEntity getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(AddressEntity fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    private AddressEntity toAddress;
    private AddressEntity fromAddress;

    private String trackingNumber;

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public int getTradeGameId() {
        return tradeGameId;
    }

    public void setTradeGameId(int tradeGameId) {
        this.tradeGameId = tradeGameId;
    }

    public int getSenderStatus() {
        return senderStatus;
    }

    public void setSenderStatus(int senderStatus) {
        this.senderStatus = senderStatus;
    }

    public int getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(int receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

}
