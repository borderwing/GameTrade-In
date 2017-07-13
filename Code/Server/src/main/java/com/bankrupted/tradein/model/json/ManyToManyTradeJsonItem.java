package com.bankrupted.tradein.model.json;

/**
 * Created by homepppp on 2017/7/13.
 */
public class ManyToManyTradeJsonItem {
    /*
           {
                "YouWantGames":"gameid1,gameid2,"
                "YouOfferGames":"gameid1,gameid2,..."
                "TargetUserId":1,
                "addressId":1,
                "pointRange":1
           }
     */
    private String YouWantGames;
    private String YouOfferGames;
    private int TargetUserId;
    private int addressId;
    private int pointRange;

    public String getYouWantGames() {
        return YouWantGames;
    }

    public void setYouWantGames(String youWantGames) {
        YouWantGames = youWantGames;
    }

    public String getYouOfferGames() {
        return YouOfferGames;
    }

    public void setYouOfferGames(String youOfferGames) {
        YouOfferGames = youOfferGames;
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
