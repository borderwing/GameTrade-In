package model;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/27.
 */
@Entity
@Table(name = "tradeinfo", schema = "tradein", catalog = "")
public class TradeInfoEntity {
    private int tradeGameId;
    private Integer senderId;
    private Integer receiverId;
    private int fromAddressId;
    private int toAddressId;
    private Integer transferAddressId;
    private TradeGameEntity tradegamesByTradeGameId;
    private UserEntity usersBySenderId;
    private UserEntity usersByReceiverId;
    private AddressEntity addressesByFromAddressId;
    private AddressEntity addressesByToAddressId;
    private AddressEntity addressesByTransferAddressId;

    @Id
    @Column(name = "tradeGameID", nullable = false)
    public int getTradeGameId() {
        return tradeGameId;
    }

    public void setTradeGameId(int tradeGameId) {
        this.tradeGameId = tradeGameId;
    }

    @Basic
    @Column(name = "senderID", nullable = true)
    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    @Basic
    @Column(name = "receiverID", nullable = true)
    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    @Basic
    @Column(name = "fromAddressID", nullable = false)
    public int getFromAddressId() {
        return fromAddressId;
    }

    public void setFromAddressId(int fromAddressId) {
        this.fromAddressId = fromAddressId;
    }

    @Basic
    @Column(name = "toAddressID", nullable = false)
    public int getToAddressId() {
        return toAddressId;
    }

    public void setToAddressId(int toAddressId) {
        this.toAddressId = toAddressId;
    }

    @Basic
    @Column(name = "transferAddressID", nullable = true)
    public Integer getTransferAddressId() {
        return transferAddressId;
    }

    public void setTransferAddressId(Integer transferAddressId) {
        this.transferAddressId = transferAddressId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeInfoEntity that = (TradeInfoEntity) o;

        if (tradeGameId != that.tradeGameId) return false;
        if (fromAddressId != that.fromAddressId) return false;
        if (toAddressId != that.toAddressId) return false;
        if (senderId != null ? !senderId.equals(that.senderId) : that.senderId != null) return false;
        if (receiverId != null ? !receiverId.equals(that.receiverId) : that.receiverId != null) return false;
        if (transferAddressId != null ? !transferAddressId.equals(that.transferAddressId) : that.transferAddressId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tradeGameId;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (receiverId != null ? receiverId.hashCode() : 0);
        result = 31 * result + fromAddressId;
        result = 31 * result + toAddressId;
        result = 31 * result + (transferAddressId != null ? transferAddressId.hashCode() : 0);
        return result;
    }

    @OneToOne
    @JoinColumn(name = "tradeGameID", referencedColumnName = "tradeGameID", nullable = false)
    public TradeGameEntity getTradegamesByTradeGameId() {
        return tradegamesByTradeGameId;
    }

    public void setTradegamesByTradeGameId(TradeGameEntity tradegamesByTradeGameId) {
        this.tradegamesByTradeGameId = tradegamesByTradeGameId;
    }

    @ManyToOne
    @JoinColumn(name = "senderID", referencedColumnName = "userID")
    public UserEntity getUsersBySenderId() {
        return usersBySenderId;
    }

    public void setUsersBySenderId(UserEntity usersBySenderId) {
        this.usersBySenderId = usersBySenderId;
    }

    @ManyToOne
    @JoinColumn(name = "receiverID", referencedColumnName = "userID")
    public UserEntity getUsersByReceiverId() {
        return usersByReceiverId;
    }

    public void setUsersByReceiverId(UserEntity usersByReceiverId) {
        this.usersByReceiverId = usersByReceiverId;
    }

    @ManyToOne
    @JoinColumn(name = "fromAddressID", referencedColumnName = "addressID", nullable = false)
    public AddressEntity getAddressesByFromAddressId() {
        return addressesByFromAddressId;
    }

    public void setAddressesByFromAddressId(AddressEntity addressesByFromAddressId) {
        this.addressesByFromAddressId = addressesByFromAddressId;
    }

    @ManyToOne
    @JoinColumn(name = "toAddressID", referencedColumnName = "addressID", nullable = false)
    public AddressEntity getAddressesByToAddressId() {
        return addressesByToAddressId;
    }

    public void setAddressesByToAddressId(AddressEntity addressesByToAddressId) {
        this.addressesByToAddressId = addressesByToAddressId;
    }

    @ManyToOne
    @JoinColumn(name = "transferAddressID", referencedColumnName = "addressID")
    public AddressEntity getAddressesByTransferAddressId() {
        return addressesByTransferAddressId;
    }

    public void setAddressesByTransferAddressId(AddressEntity addressesByTransferAddressId) {
        this.addressesByTransferAddressId = addressesByTransferAddressId;
    }
}
