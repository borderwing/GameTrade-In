package model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.*;

import javax.persistence.*;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by homepppp on 2017/6/28.
 */
@Embeddable
public class WishEntityPK implements Serializable {
    private GameEntity game;
    private UserEntity user;

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

        WishEntityPK that = (WishEntityPK) o;

        if (game != null ? !game.equals(that.game) : that.game != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (game != null ? game.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
