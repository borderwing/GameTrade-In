package com.bankrupted.tradein.model.temporaryItem;

public class SeniorMatchResultItem {

    private int userAId;
    private int userBId;
    private String userAOffer;
    private String userBOffer;
    private String youOffer;

    public int getUserAId() {
        return userAId;
    }

    public void setUserAId(int userAId) {
        this.userAId = userAId;
    }

    public int getUserBId() {
        return userBId;
    }

    public void setUserBId(int userBId) {
        this.userBId = userBId;
    }

    public String getUserAOffer() {
        return userAOffer;
    }

    public void setUserAOffer(String userAOffer) {
        this.userAOffer = userAOffer;
    }

    public String getUserBOffer() {
        return userBOffer;
    }

    public void setUserBOffer(String userBOffer) {
        this.userBOffer = userBOffer;
    }

    public String getYouOffer() {
        return youOffer;
    }

    public void setYouOffer(String youOffer) {
        this.youOffer = youOffer;
    }
}
