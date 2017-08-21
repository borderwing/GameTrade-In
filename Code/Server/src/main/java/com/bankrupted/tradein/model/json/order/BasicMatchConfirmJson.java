package com.bankrupted.tradein.model.json.order;

/**
 * Created by homepppp on 2017/7/18.
 */
public class BasicMatchConfirmJson {
    private String youWantGames;
    private String youOfferGames;
    private int targetUserId;
    private int addressid;

    public String getYouWantGames() {
        return youWantGames;
    }

    public void setYouWantGames(String youWantGames) {
        this.youWantGames = youWantGames;
    }

    public String getYouOfferGames() {
        return youOfferGames;
    }

    public void setYouOfferGames(String youOfferGames) {
        this.youOfferGames = youOfferGames;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getAddressid() {
        return addressid;
    }

    public void setAddressid(int addressid) {
        this.addressid = addressid;
    }
}
