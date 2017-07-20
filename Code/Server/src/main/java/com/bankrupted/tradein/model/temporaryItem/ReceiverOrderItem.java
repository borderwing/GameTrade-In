package com.bankrupted.tradein.model.temporaryItem;

/**
 * Created by homepppp on 2017/7/4.
 */
public class ReceiverOrderItem {
    private int senderId;
    private long getGameId;
    private long offerGameId;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public long getGetGameId() {
        return getGameId;
    }

    public void setGetGameId(long getGameId) {
        this.getGameId = getGameId;
    }

    public long getOfferGameId() {
        return offerGameId;
    }

    public void setOfferGameId(long offerGameId) {
        this.offerGameId = offerGameId;
    }
}
