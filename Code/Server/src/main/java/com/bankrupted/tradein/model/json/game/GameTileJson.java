package com.bankrupted.tradein.model.json.game;

import java.util.List;

/**
 * Created by lykav on 7/14/2017.
 */
public class GameTileJson {
    private long igdbId;
    private String title;
    private List<String> platforms;

    private float popularity;
    private String coverUrl;

    public long getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(long igdbId) {
        this.igdbId = igdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
