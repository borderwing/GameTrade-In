package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.GameEntity;

public class OrderResult {
    private GameEntity wishGame;
    private GameEntity offerGame;
    private int wishPoints;
    private int offerPoints;
    private int status;

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
}
