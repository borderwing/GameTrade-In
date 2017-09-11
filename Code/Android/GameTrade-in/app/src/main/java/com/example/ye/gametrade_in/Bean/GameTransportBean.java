package com.example.ye.gametrade_in.Bean;

/**
 * Created by ye on 2017/9/11.
 */

public class GameTransportBean {
    public Long igdbId;
    public int platformId;
    public int regionId;
    public int points;


    public GameTransportBean(Long igdbId, int platformId, int regionId, int points) {
        this.igdbId = igdbId;
        this.platformId = platformId;
        this.regionId = regionId;
        this.points = points;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "GameTransportBean{" +
                "igdbId=" + igdbId +
                ", platformId=" + platformId +
                ", regionId=" + regionId +
                ", points=" + points +
                '}';
    }
}
