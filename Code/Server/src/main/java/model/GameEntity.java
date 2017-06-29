package model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/27.
 */
@Entity
@Table(name = "games", schema = "tradein", catalog = "")
public class GameEntity {
    private int gameId;
    private String title;
    private String platform;
    private String language;
    private String genre;
    private Integer evaluatePoint;

    private Collection<OfferEntity> offersByGameId;
    private Collection<TradeGameEntity> tradegamesByGameId;
    private Collection<WisheEntity> wishesByGameId;

    @Id
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

    @OneToMany(mappedBy = "gamesByGameId")
    public Collection<OfferEntity> getOffersByGameId() {
        return offersByGameId;
    }

    public void setOffersByGameId(Collection<OfferEntity> offersByGameId) {
        this.offersByGameId = offersByGameId;
    }

    @OneToMany(mappedBy = "gamesByGameId")
    public Collection<TradeGameEntity> getTradegamesByGameId() {
        return tradegamesByGameId;
    }

    public void setTradegamesByGameId(Collection<TradeGameEntity> tradegamesByGameId) {
        this.tradegamesByGameId = tradegamesByGameId;
    }

    @OneToMany(mappedBy = "gamesByGameId")
    public Collection<WisheEntity> getWishesByGameId() {
        return wishesByGameId;
    }

    public void setWishesByGameId(Collection<WisheEntity> wishesByGameId) {
        this.wishesByGameId = wishesByGameId;
    }
}
