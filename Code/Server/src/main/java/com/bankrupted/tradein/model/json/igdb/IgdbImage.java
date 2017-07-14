package com.bankrupted.tradein.model.json.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lykav on 7/14/2017.
 */
public class IgdbImage {

    private String url;

    @JsonProperty(value = "cloudinary_id")
    private String cloudinaryId;

    private int width;
    private int height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCloudinaryId() {
        return cloudinaryId;
    }

    public void setCloudinaryId(String cloudinaryId) {
        this.cloudinaryId = cloudinaryId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}