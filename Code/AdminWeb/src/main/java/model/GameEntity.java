package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "games", catalog = "")
public class GameEntity {
    private long gameId;

    private long igdbId;
    private int platformId;
    private int regionId;

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
    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }


    // columns for IGDB database
    public long getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(long igdbId) {
        this.igdbId = igdbId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
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

        return gameId == that.gameId;
    }

    @Override
    public int hashCode() {
        return (int) (gameId ^ (gameId >>> 32));
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
