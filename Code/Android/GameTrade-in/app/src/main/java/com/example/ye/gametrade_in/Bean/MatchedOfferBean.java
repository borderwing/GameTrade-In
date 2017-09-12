package com.example.ye.gametrade_in.Bean;

/**
 * Created by lykav on 9/12/2017.
 */

public class MatchedOfferBean {
    private Integer offerPoint;

    private UserBean sender;
    private GameBean wishGame;
    private GameBean offerGame;

    public Integer getOfferPoint() {
        return offerPoint;
    }

    public void setOfferPoint(Integer offerPoint) {
        this.offerPoint = offerPoint;
    }

    public UserBean getSender() {
        return sender;
    }

    public void setSender(UserBean sender) {
        this.sender = sender;
    }

    public GameBean getWishGame() {
        return wishGame;
    }

    public void setWishGame(GameBean wishGame) {
        this.wishGame = wishGame;
    }

    public GameBean getOfferGame() {
        return offerGame;
    }

    public void setOfferGame(GameBean offerGame) {
        this.offerGame = offerGame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchedOfferBean that = (MatchedOfferBean) o;

        if (offerPoint != null ? !offerPoint.equals(that.offerPoint) : that.offerPoint != null)
            return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
        if (wishGame != null ? !wishGame.equals(that.wishGame) : that.wishGame != null)
            return false;
        return offerGame != null ? offerGame.equals(that.offerGame) : that.offerGame == null;

    }

    @Override
    public int hashCode() {
        int result = offerPoint != null ? offerPoint.hashCode() : 0;
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (wishGame != null ? wishGame.hashCode() : 0);
        result = 31 * result + (offerGame != null ? offerGame.hashCode() : 0);
        return result;
    }
}
