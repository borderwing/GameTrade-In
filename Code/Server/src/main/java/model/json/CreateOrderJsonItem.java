package model.json;

/**
 * Created by homepppp on 2017/7/5.
 */
public class CreateOrderJsonItem {
    private int senderGameId;
    private int targetUserId;
    private int addressId;

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getSenderGameId() {
        return senderGameId;
    }

    public void setSenderGameId(int senderGameId) {
        this.senderGameId = senderGameId;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }
}
