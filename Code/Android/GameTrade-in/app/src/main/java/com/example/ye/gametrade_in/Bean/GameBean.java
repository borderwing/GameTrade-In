package com.example.ye.gametrade_in.Bean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by ye on 2017/7/4.
 */

public class GameBean {

    private Long gameId;
    private Long igdbId;

    private int platformId;
    private int regionId;

    private int evaluatePoint;



    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }


    public Long getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Long igdbId) {
        this.igdbId = igdbId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getEvaluatePoint() {
        return evaluatePoint;
    }

    public void setEvaluatePoint(int evaluatePoint) {
        this.evaluatePoint = evaluatePoint;
    }
}
