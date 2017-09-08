package model;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "tradegames", catalog = "")
public class TradeGameEntity {
    private int trade_gameid;
    private String trackingNumber;
    private Integer points;
    private Integer status;

    private GameEntity game;

    private TradeOrderEntity tradeOrder;

    private UserEntity sender;
    private AddressEntity from_addressid;
    private Integer sender_status = 0;

    private UserEntity receiver;
    private AddressEntity to_addressid;
    private Integer receiver_status = 0;


    private AddressEntity transferAddress;


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "trade_gameid", nullable = false)
    public int getTrade_gameid() {
        return trade_gameid;
    }

    public void setTrade_gameid(int trade_gameid) {
        this.trade_gameid = trade_gameid;
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

    @Basic
    @Column(name = "sender_status", nullable = true)
    public Integer getSender_status() {
        return sender_status;
    }

    public void setSender_status(Integer sender_status) {
        this.sender_status = sender_status;
    }

    @Basic
    @Column(name = "receiver_status", nullable = true)
    public Integer getReceiver_status() {
        return receiver_status;
    }

    public void setReceiver_status(Integer receiver_status) {
        this.receiver_status = receiver_status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeGameEntity that = (TradeGameEntity) o;

        if (trade_gameid != that.trade_gameid) return false;
        if (trackingNumber != null ? !trackingNumber.equals(that.trackingNumber) : that.trackingNumber != null)
            return false;
        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = trade_gameid;
        result = 31 * result + (trackingNumber != null ? trackingNumber.hashCode() : 0);
        result = 31 * result + (points != null ? points.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "gameID", referencedColumnName = "gameID", nullable = false)
    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
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
    @JoinColumn(name = "from_addressid", referencedColumnName = "addressID", nullable = true)
    public AddressEntity getFrom_addressid() {
        return from_addressid;
    }

    public void setFrom_addressid(AddressEntity from_addressid) {
        this.from_addressid = from_addressid;
    }

    @ManyToOne
    @JoinColumn(name = "to_addressid", referencedColumnName = "addressID", nullable = true)
    public AddressEntity getTo_addressid() {
        return to_addressid;
    }

    public void setTo_addressid(AddressEntity to_addressid) {
        this.to_addressid = to_addressid;
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
