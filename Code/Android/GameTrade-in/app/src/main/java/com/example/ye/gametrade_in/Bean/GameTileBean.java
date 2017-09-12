package com.example.ye.gametrade_in.Bean;

import java.util.List;

/**
 * Created by ye on 2017/7/19.
 */

public class GameTileBean {
    private long igdbId;
    private String title;
    private List<String> platforms;

    private float popularity;
    private String coverUrl;

    private String summary;

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
