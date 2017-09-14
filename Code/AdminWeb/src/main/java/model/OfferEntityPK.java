package model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
    private Timestamp create_time;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "create_time")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("create_time")
    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }


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
        return create_time != null ? create_time.equals(that.create_time) : that.create_time == null;
    }

    @Override
    public int hashCode() {
        int result = game != null ? game.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (create_time != null ? create_time.hashCode() : 0);
        return result;
    }
}
