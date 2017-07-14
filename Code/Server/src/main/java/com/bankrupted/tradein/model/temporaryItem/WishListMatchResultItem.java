package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.GameEntity;

import java.util.List;

/**
 * Created by homepppp on 2017/7/13.
 */
public class WishListMatchResultItem {
    private List<GameEntity> YouOfferGame;
    private int TargetUserId;
    private List<GameEntity> YouWantGame;

    public List<GameEntity> getYouOfferGame() {
        return YouOfferGame;
    }

    public void setYouOfferGame(List<GameEntity> youOfferGame) {
        YouOfferGame = youOfferGame;
    }

    public int getTargetUserId() {
        return TargetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        TargetUserId = targetUserId;
    }

    public List<GameEntity> getYouWantGame() {
        return YouWantGame;
    }

    public void setYouWantGame(List<GameEntity> youWantGame) {
        YouWantGame = youWantGame;
    }
}
