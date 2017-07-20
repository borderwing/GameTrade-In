package com.bankrupted.tradein.model.json.offer;

/**
 * Created by homepppp on 2017/7/4.
 */
public class CreateOfferJson {
    private long gameId;
    private int points;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
