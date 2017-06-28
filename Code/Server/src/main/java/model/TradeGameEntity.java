package model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "tradegames", catalog = "")
public class TradeGameEntity {
    private int tradeGameId;
    private String trackingNumber;
    private Integer points;
    private Integer status;

    private GamesEntity game;

    private TradeOrderEntity tradeOrder;

    private UserEntity sender;
    private UserEntity receiver;
    private AddressEntity fromAddress;
    private AddressEntity toAddress;
    private AddressEntity transferAddress;


    @Id
    @Column(name = "tradeGameID", nullable = false)
    public int getTradeGameId() {
        return tradeGameId;
    }

    public void setTradeGameId(int tradeGameId) {
        this.tradeGameId = tradeGameId;
    }

    @Basic
    @Column(name = "trackingNumber", nullable = true, length = 1024)
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Basic
    @Column(name = "points", nullable = true)
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Basic
    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeGameEntity that = (TradeGameEntity) o;

        if (tradeGameId != that.tradeGameId) return false;
        if (trackingNumber != null ? !trackingNumber.equals(that.trackingNumber) : that.trackingNumber != null)
            return false;
        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tradeGameId;
        result = 31 * result + (trackingNumber != null ? trackingNumber.hashCode() : 0);
        result = 31 * result + (points != null ? points.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "gameID", referencedColumnName = "gameID", nullable = false)
    public GamesEntity getGame() {
        return game;
    }

    public void setGame(GamesEntity game) {
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "tradeOrderID", referencedColumnName = "tradeOrderID")
    public TradeOrderEntity getTradeOrder() {
        return tradeOrder;
    }

    public void setTradeOrder(TradeOrderEntity tradeOrder) {
        this.tradeOrder = tradeOrder;
    }

    @ManyToOne
    @JoinColumn(name = "senderID", referencedColumnName = "userID")
    public UserEntity getSender() {
        return sender;
    }
    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    @ManyToOne
    @JoinColumn(name = "receiverID", referencedColumnName = "userID")
    public UserEntity getReceiver() {
        return receiver;
    }
    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    @ManyToOne
    @JoinColumn(name = "fromAddressID", referencedColumnName = "addressID", nullable = false)
    public AddressEntity getFromAddress() {
        return fromAddress;
    }
    public void setFromAddress(AddressEntity fromAddress) {
        this.fromAddress = fromAddress;
    }


    @ManyToOne
    @JoinColumn(name = "toAddressID", referencedColumnName = "addressID", nullable = false)
    public AddressEntity getToAddress() {
        return toAddress;
    }

    public void setToAddress(AddressEntity toAddress) {
        this.toAddress = toAddress;
    }


    @ManyToOne
    @JoinColumn(name = "transferAddressID", referencedColumnName = "addressID")
    public AddressEntity getTransferAddress() {
        return transferAddress;
    }

    public void setTransferAddress(AddressEntity transferAddress) {
        this.transferAddress = transferAddress;
    }

}
