package model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "offers", catalog = "")
public class OfferEntity {

    private OfferEntityPK offerEntityPK = new OfferEntityPK();

    private Integer points;
    private Integer status;
    private Timestamp createTime;


    @EmbeddedId
    public OfferEntityPK getOfferEntityPK(){
        return offerEntityPK;
    }

    public void setOfferEntityPK(OfferEntityPK offerEntityPK){
        this.offerEntityPK = offerEntityPK;
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
    @Column(name = "createtime", nullable = true)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferEntity that = (OfferEntity) o;

        if (offerEntityPK != that.offerEntityPK) return false;
        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (getOfferEntityPK() != null ? getOfferEntityPK().hashCode() : 0);
    }

    @Transient
    public GameEntity getGame() {
        return offerEntityPK.getGame();
    }

    public void setGame(GameEntity game) {
        offerEntityPK.setGame(game);
    }

    @Transient
    public UserEntity getUser() {
        return offerEntityPK.getUser();
    }

    public void setUser(UserEntity user) {
        offerEntityPK.setUser(user);
    }

}
