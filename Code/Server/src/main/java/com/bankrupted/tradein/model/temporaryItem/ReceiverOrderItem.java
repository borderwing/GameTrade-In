package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.GameEntity;

/**
 * Created by homepppp on 2017/7/4.
 */
public class ReceiverOrderItem {
    private int senderId;
    private GameEntity wishGame;
    private GameEntity offerGame;
    private int offerPoint;


    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    public int getOfferPoint() {
        return offerPoint;
    }

    public void setOfferPoint(int offerPoint) {
        this.offerPoint = offerPoint;
    }
}
