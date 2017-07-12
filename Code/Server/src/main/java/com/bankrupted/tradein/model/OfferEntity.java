package com.bankrupted.tradein.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "offers", catalog = "")
public class OfferEntity {

    private OfferEntityPK offerEntityPK = new OfferEntityPK();

    private Integer points;
    private Integer status;


    @EmbeddedId
    @JsonProperty("pair")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferEntity that = (OfferEntity) o;

        if (offerEntityPK != null ? !offerEntityPK.equals(that.offerEntityPK) : that.offerEntityPK != null)
            return false;
        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = offerEntityPK != null ? offerEntityPK.hashCode() : 0;
        result = 31 * result + (points != null ? points.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
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
