package com.bankrupted.tradein.model.json.order;

public class SeniorMatchConfirmJson {
    private int UserAId;
    private int UserBId;
    private String UserAOffer;
    private String UserBOffer;
    private String YouOffer;
    private int addresssId;

    public int getUserAId() {
        return UserAId;
    }

    public void setUserAId(int userAId) {
        UserAId = userAId;
    }

    public int getUserBId() {
        return UserBId;
    }

    public void setUserBId(int userBId) {
        UserBId = userBId;
    }

    public String getUserAOffer() {
        return UserAOffer;
    }

    public void setUserAOffer(String userAOffer) {
        UserAOffer = userAOffer;
    }

    public String getUserBOffer() {
        return UserBOffer;
    }

    public void setUserBOffer(String userBOffer) {
        UserBOffer = userBOffer;
    }

    public String getYouOffer() {
        return YouOffer;
    }

    public void setYouOffer(String youOffer) {
        YouOffer = youOffer;
    }

    public int getAddresssId() {
        return addresssId;
    }

    public void setAddresssId(int addresssId) {
        this.addresssId = addresssId;
    }
}
