package com.bankrupted.tradein.model.json.order;

/**
 * Created by homepppp on 2017/7/5.
 */
public class ConfirmMatchJson {
    private long gameId;
    private int targetUserId;
    private int addressId;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }
}
