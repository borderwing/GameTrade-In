package com.bankrupted.tradein.model.temporaryItem;

import com.bankrupted.tradein.model.GameEntity;

import java.util.List;

/**
 * Created by homepppp on 2017/7/13.
 */
public class WishListMatchResultItem {
    private String YouOfferGame;
    private int TargetUserId;
    private String YouWantGame;

    public String getYouOfferGame() {
        return YouOfferGame;
    }

    public void setYouOfferGame(String youOfferGame) {
        YouOfferGame = youOfferGame;
    }

    public int getTargetUserId() {
        return TargetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        TargetUserId = targetUserId;
    }

    public String getYouWantGame() {
        return YouWantGame;
    }

    public void setYouWantGame(String youWantGame) {
        YouWantGame = youWantGame;
    }
}
