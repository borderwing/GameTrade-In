package com.bankrupted.tradein.model.json;

/**
 * Created by homepppp on 2017/7/13.
 */
public class ManyToManyTradeJsonItem {
    /*
           {
                "youWantGames":"gameid1,gameid2,"
                "youOfferGames":"gameid1,gameid2,..."
                "pointRange":1
           }
     */
    private String youWantGames;
    private String youOfferGames;
    private int pointRange;

    public String getYouWantGames() {
        return youWantGames;
    }

    public void setYouWantGames(String youWantGames) {
        this.youWantGames = youWantGames;
    }

    public String getYouOfferGames() {
        return youOfferGames;
    }

    public void setYouOfferGames(String youOfferGames) {
        this.youOfferGames = youOfferGames;
    }

    public int getPointRange() {
        return pointRange;
    }

    public void setPointRange(int pointRange) {
        this.pointRange = pointRange;
    }
}
