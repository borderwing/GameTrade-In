package com.bankrupted.tradein.model.json.igdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lykav on 7/14/2017.
 */
public class IgdbImage {

    @JsonIgnore
    private static final String entry = "https://images.igdb.com/igdb/image/upload";

    // array for storing all possible size values
    @JsonIgnore
    private static final String[] sizeValueArray = new String[]{
            "cover_small", "cover_small_2x",
            "cover_big", "cover_big_2x",
            "screenshot_med", "screenshot_med_2x",
            "screenshot_big", "screenshot_big_2x",
            "screenshot_huge", "screenshot_huge_2x",
            "logo_med", "logo_med_2x",
            "thumb", "thumb_2x",
            "micro", "micro_2x"
    };
    @JsonIgnore
    private static final Set<String> sizeValueSet = new HashSet<String>(Arrays.asList(sizeValueArray));

    private String url;

    @JsonProperty(value = "cloudinary_id")
    private String cloudinaryId;

    private int width;
    private int height;

    public String getUrl() {
        return "https:" + url;
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


    // return the url for the image with the given size (defined above in the array)
    // if cannot find matching size, return the original url for the image
    public String getUrlBySize(final String size){
        if(cloudinaryId == null || cloudinaryId == ""){
            return "";
        } else {
            if(sizeValueSet.contains(size)){
                return entry + "/t_" + size + "/" + cloudinaryId + ".jpg";
            } else {
                return entry + "/" + cloudinaryId + ".jpg";
            }
        }
    }
}