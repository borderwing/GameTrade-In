package com.bankrupted.tradein.model.json.igdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lykav on 7/14/2017.
 */
public class IgdbGame {
    private long id;
    private String name;
    private float popularity;
    private String summary;

    @JsonProperty(value = "release_dates")
    private List<IgdbRelease> releaseDates;

    private IgdbImage cover;
    private List<IgdbImage> screenshots;

    private List<Long> genres;
    private List<Long> themes;
    private List<Long> keywords;

    private String url;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public List<IgdbRelease> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<IgdbRelease> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public IgdbImage getCover() {
        return cover;
    }

    public void setCover(IgdbImage cover) {
        this.cover = cover;
    }

    public List<IgdbImage> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<IgdbImage> screenshots) {
        this.screenshots = screenshots;
    }

    public List<Long> getGenres() {
        return genres;
    }
    @JsonIgnore
    public List<Long> getLongGenres(){
        return genres;
    }

    public void setGenres(List<Long> genres) {
        this.genres = genres;
    }

    public List<Long> getThemes() {
        return themes;
    }

    public void setThemes(List<Long> themes) {
        this.themes = themes;
    }

    public List<Long> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Long> keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
