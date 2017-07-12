package com.bankrupted.tradein.model.temporaryItem;

/**
 * Created by homepppp on 2017/7/10.
 */
public class PotentialChangesItem {
    private int userAId;
    private int userBId;
    private int UserASendGameId;
    private int UserBSendGameId;

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

    public int getUserASendGameId() {
        return UserASendGameId;
    }

    public void setUserASendGameId(int userASendGameId) {
        UserASendGameId = userASendGameId;
    }

    public int getUserBSendGameId() {
        return UserBSendGameId;
    }

    public void setUserBSendGameId(int userBSendGameId) {
        UserBSendGameId = userBSendGameId;
    }
}
