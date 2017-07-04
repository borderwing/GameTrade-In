package model.temporaryItem;

/**
 * Created by homepppp on 2017/7/4.
 */
public class ReceiverOrderItem {
    private int senderId;
    private int getGameId;
    private int offerGameId;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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
