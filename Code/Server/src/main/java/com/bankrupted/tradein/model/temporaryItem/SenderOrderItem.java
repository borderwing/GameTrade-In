package com.bankrupted.tradein.model.temporaryItem;

/**
 * Created by homepppp on 2017/7/4.
 */
public class SenderOrderItem {
    private int receiverId;
    private long getGameId;
    private long offerGameId;

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
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
