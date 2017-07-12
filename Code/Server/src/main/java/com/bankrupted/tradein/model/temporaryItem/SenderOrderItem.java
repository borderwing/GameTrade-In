package com.bankrupted.tradein.model.temporaryItem;

/**
 * Created by homepppp on 2017/7/4.
 */
public class SenderOrderItem {
    private int receiverId;
    private int getGameId;
    private int offerGameId;

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getGetGameId() {
        return getGameId;
    }

    public void setGetGameId(int getGameId) {
        this.getGameId = getGameId;
    }

    public int getOfferGameId() {
        return offerGameId;
    }

    public void setOfferGameId(int offerGameId) {
        this.offerGameId = offerGameId;
    }
}
