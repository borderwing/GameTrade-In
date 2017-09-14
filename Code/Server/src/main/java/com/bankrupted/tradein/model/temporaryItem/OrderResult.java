package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.AddressEntity;
import com.bankrupted.tradein.model.GameEntity;
import com.sun.jndi.cosnaming.IiopUrl;

public class OrderResult {
    private int orderId;
    private GameEntity wishGame;
    private GameEntity offerGame;
    private int wishPoints;
    private int offerPoints;
    private int status;
    private AddressEntity YouAddress;
    private AddressEntity TargetAddress;

    private String otherUsername;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public GameEntity getWishGame() {
        return wishGame;
    }

    public void setWishGame(GameEntity wishGame) {
        this.wishGame = wishGame;
    }

    public GameEntity getOfferGame() {
        return offerGame;
    }

    public void setOfferGame(GameEntity offerGame) {
        this.offerGame = offerGame;
    }

    public int getWishPoints() {
        return wishPoints;
    }

    public void setWishPoints(int wishPoints) {
        this.wishPoints = wishPoints;
    }

    public int getOfferPoints() {
        return offerPoints;
    }

    public void setOfferPoints(int offerPoints) {
        this.offerPoints = offerPoints;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AddressEntity getYouAddress() {
        return YouAddress;
    }

    public void setYouAddress(AddressEntity youAddress) {
        YouAddress = youAddress;
    }

    public AddressEntity getTargetAddress() {
        return TargetAddress;
    }

    public void setTargetAddress(AddressEntity targetAddress) {
        TargetAddress = targetAddress;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }
}
