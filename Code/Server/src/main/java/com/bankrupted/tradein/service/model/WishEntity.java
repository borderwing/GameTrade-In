package com.bankrupted.tradein.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "wishes", catalog = "")
public class WishEntity {

    private WishEntityPK wishEntityPK = new WishEntityPK();

    private Integer points;
    private Integer status;
    //private Timestamp createTime;


    @EmbeddedId
    @JsonProperty("pair")
    public WishEntityPK getWishEntityPK() {
        return wishEntityPK;
    }

    public void setWishEntityPK(WishEntityPK wishEntityPK) {
        this.wishEntityPK = wishEntityPK;
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

    /*@Basic
    @Column(name = "createtime", nullable = true)
    @CreationTimestamp
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishEntity that = (WishEntity) o;

        if (getWishEntityPK() != null ? !getWishEntityPK().equals(that.getWishEntityPK())
                : that.getWishEntityPK() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (getWishEntityPK() != null ? getWishEntityPK().hashCode() : 0);
    }

    @Transient
    public GameEntity getGame() {
        return wishEntityPK.getGame();
    }

    public void setGame(GameEntity game) {
        wishEntityPK.setGame(game);
    }

    @Transient
    public UserEntity getUser() {
        return wishEntityPK.getUser();
    }

    public void setUser(UserEntity user) {
        wishEntityPK.setUser(user);
    }



}
