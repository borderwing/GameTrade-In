package com.bankrupted.tradein.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "games", catalog = "")
public class GameEntity {
    private int gameId;
    private String title;
    private String platform;
    private String language;
    private String genre;

    //default value need to be changed later
    private Integer evaluatePoint=100;

    @JsonIgnore
    private Collection<WishEntity> wishes;
    @JsonIgnore
    private Collection<OfferEntity> offers;
    @JsonIgnore
    private Collection<TradeGameEntity> tradeGames;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "gameID", nullable = false)
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Basic
    @Column(name = "title", nullable = true, length = 1024)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "platform", nullable = true, length = 1024)
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Basic
    @Column(name = "language", nullable = true, length = 1024)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Basic
    @Column(name = "genre", nullable = true, length = 1024)
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Basic
    @Column(name = "evaluatePoint", nullable = true)
    public Integer getEvaluatePoint() {
        return evaluatePoint;
    }

    public void setEvaluatePoint(Integer evaluatePoint) {
        this.evaluatePoint = evaluatePoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameEntity that = (GameEntity) o;

        if (gameId != that.gameId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (platform != null ? !platform.equals(that.platform) : that.platform != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        if (genre != null ? !genre.equals(that.genre) : that.genre != null) return false;
        if (evaluatePoint != null ? !evaluatePoint.equals(that.evaluatePoint) : that.evaluatePoint != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gameId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (evaluatePoint != null ? evaluatePoint.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "wishEntityPK.game")
    public Collection<WishEntity> getWishes() {
        return wishes;
    }

    public void setWishes(Collection<WishEntity> _wishes) {
        wishes = _wishes;
    }

    @OneToMany(mappedBy = "offerEntityPK.game")
    public Collection<OfferEntity> getOffers() {
        return offers;
    }

    public void setOffers(Collection<OfferEntity> _offers) {
        offers = _offers;
    }

    @OneToMany(mappedBy = "game")
    public Collection<TradeGameEntity> getTradeGames() {
        return tradeGames;
    }

    public void setTradeGames(Collection<TradeGameEntity> _tradeGames) {
        tradeGames = _tradeGames;
    }
}
