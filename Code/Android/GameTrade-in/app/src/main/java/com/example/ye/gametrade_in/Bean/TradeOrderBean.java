package com.example.ye.gametrade_in.Bean;

/**
 * Created by lykav on 9/13/2017.
 */

public class TradeOrderBean {
    private Long orderId;
    private GameBean wishGame;
    private GameBean offerGame;
    private int wishPoints;
    private int offerPoints;
    private int status;
    private AddressBean youAddress;
    private AddressBean targetAddress;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public GameBean getWishGame() {
        return wishGame;
    }

    public void setWishGame(GameBean wishGame) {
        this.wishGame = wishGame;
    }

    public GameBean getOfferGame() {
        return offerGame;
    }

    public void setOfferGame(GameBean offerGame) {
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

    public AddressBean getYouAddress() {
        return youAddress;
    }

    public void setYouAddress(AddressBean youAddress) {
        this.youAddress = youAddress;
    }

    public AddressBean getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(AddressBean targetAddress) {
        this.targetAddress = targetAddress;
    }
}
