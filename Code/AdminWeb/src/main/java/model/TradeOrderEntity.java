package model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "tradeorders", catalog = "")
public class TradeOrderEntity {
    private int tradeOrderId;
    private Timestamp createTime;
    private Integer status;
    private Collection<TradeGameEntity> tradeGames;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "tradeOrderID", nullable = false)
    public int getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(int tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    @Basic
    @Column(name = "createtime", nullable = true)
    public Timestamp getCreatetime() {
        return createTime;
    }

    public void setCreatetime(Timestamp createTime) {
        this.createTime = createTime;
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

        TradeOrderEntity that = (TradeOrderEntity) o;

        if (tradeOrderId != that.tradeOrderId) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tradeOrderId;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "tradeOrder")
    public Collection<TradeGameEntity> getTradeGames() {
        return tradeGames;
    }

    public void setTradeGames(Collection<TradeGameEntity> tradeGames) {
        this.tradeGames = tradeGames;
    }
}
