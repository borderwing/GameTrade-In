package model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by homepppp on 2017/6/28.
 */
@Embeddable
public class OfferEntityPK implements Serializable {
    private GameEntity game;
    private UserEntity user;
    private Timestamp createTime;


    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "createTime")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("createTime")
    public Timestamp getCreateTime() {return createTime;}

    public void setCreateTime(Timestamp createTime) {this.createTime = createTime;}

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "gameId")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("gameId")
    @ManyToOne
    @JoinColumn(name = "gameID")
    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }


    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("userId")
    @ManyToOne
    @JoinColumn(name = "userID")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferEntityPK that = (OfferEntityPK) o;

        if (game != null ? !game.equals(that.game) : that.game != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;
    }

    @Override
    public int hashCode() {
        int result = game != null ? game.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
}
