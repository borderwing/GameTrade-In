package com.bankrupted.tradein.model.json;

/**
 * Created by homepppp on 2017/7/13.
 */
public class ManyToManyTradeJsonItem {
    /*
           {
                "youWantGames":"gameid1,gameid2,"
                "youOfferGames":"gameid1,gameid2,..."
                "TargetUserId":1,
                "addressId":1,
                "pointRange":1
           }
     */
    private String youWantGames;
    private String youOfferGames;
    private int TargetUserId;
    private int addressId;
    private int pointRange;

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
        return TargetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        TargetUserId = targetUserId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getPointRange() {
        return pointRange;
    }

    public void setPointRange(int pointRange) {
        this.pointRange = pointRange;
    }
}