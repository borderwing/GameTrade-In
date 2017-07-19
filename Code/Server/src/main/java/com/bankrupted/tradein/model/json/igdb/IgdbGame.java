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

    private List<Integer> genres;
    private List<Integer> themes;
    private List<Integer> keywords;


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

    public List<Integer> getGenres() {
        return genres;
    }
    @JsonIgnore
    public List<Long> getLongGenres(){
        int size = genres.size();
        List<Long> longGenres = new ArrayList<>(size);
        for(Integer genreId : genres){
            longGenres.add(genreId.longValue());
        }
        return longGenres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public List<Integer> getThemes() {
        return themes;
    }

    public void setThemes(List<Integer> themes) {
        this.themes = themes;
    }

    public List<Integer> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Integer> keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
