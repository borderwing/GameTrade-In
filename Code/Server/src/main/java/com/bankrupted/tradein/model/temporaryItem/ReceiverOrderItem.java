package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.UserEntity;

/**
 * Created by homepppp on 2017/7/4.
 */
public class ReceiverOrderItem {
    private UserEntity sender;
    private GameEntity wishGame;
    private GameEntity offerGame;
    private int offerPoint;

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
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
