package model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by homepppp on 2017/6/27.
 */
public class WisheEntityPK implements Serializable {
    private int userId;
    private int gameId;

    @Column(name = "userID", nullable = false)
    @Id
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "gameID", nullable = false)
    @Id
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WisheEntityPK that = (WisheEntityPK) o;

        if (userId != that.userId) return false;
        if (gameId != that.gameId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + gameId;
        return result;
    }
}
