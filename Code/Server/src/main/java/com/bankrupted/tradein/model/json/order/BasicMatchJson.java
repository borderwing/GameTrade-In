package com.bankrupted.tradein.model.json.order;

/**
 * Created by homepppp on 2017/7/13.
 */
public class BasicMatchJson {
    /*
           {
                "youWantGames":"gameid1,gameid2,"
                "pointRange":1
           }
     */
    private String youWantGames;
    private int pointRange;

    public String getYouWantGames() {
        return youWantGames;
    }

    public void setYouWantGames(String youWantGames) {
        this.youWantGames = youWantGames;
    }

    public int getPointRange() {
        return pointRange;
    }

    public void setPointRange(int pointRange) {
        this.pointRange = pointRange;
    }
}
