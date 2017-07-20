package com.bankrupted.tradein.model.json.igdb.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lykav on 7/20/2017.
 */
public class IgdbKeyword {
    private Long id;
    private String name;
    private String url;

    @JsonProperty(value = "games")
    List<Long> igdbGameIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Long> getIgdbGameIds() {
        return igdbGameIds;
    }

    public void setIgdbGameIds(List<Long> igdbGameIds) {
        this.igdbGameIds = igdbGameIds;
    }
}
