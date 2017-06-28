package model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by homepppp on 2017/6/27.
 */
@Entity
@Table(name = "wishes", schema = "tradein", catalog = "")
@IdClass(WisheEntityPK.class)
public class WisheEntity {
    private int userId;
    private int gameId;
    private Integer points;
    private Integer status;
    private Timestamp createtime;
    private UserEntity usersByUserId;
    private GameEntity gamesByGameId;

    @Id
    @Column(name = "userID", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Id
    @Column(name = "gameID", nullable = false)
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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
    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WisheEntity that = (WisheEntity) o;

        if (userId != that.userId) return false;
        if (gameId != that.gameId) return false;
        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (createtime != null ? !createtime.equals(that.createtime) : that.createtime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + gameId;
        result = 31 * result + (points != null ? points.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createtime != null ? createtime.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID", nullable = false)
    public UserEntity getUsersByUserId() {
        return usersByUserId;
    }

    public void setUsersByUserId(UserEntity usersByUserId) {
        this.usersByUserId = usersByUserId;
    }

    @ManyToOne
    @JoinColumn(name = "gameID", referencedColumnName = "gameID", nullable = false)
    public GameEntity getGamesByGameId() {
        return gamesByGameId;
    }

    public void setGamesByGameId(GameEntity gamesByGameId) {
        this.gamesByGameId = gamesByGameId;
    }
}
