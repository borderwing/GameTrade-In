package com.bankrupted.tradein.model.json.offer;

/**
 * Created by homepppp on 2017/7/4.
 */
public class CreateOfferJson {
    private Long igdbId;
    private int platformId;
    private int regionId;
    private int points;

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
}
