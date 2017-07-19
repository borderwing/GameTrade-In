package com.bankrupted.tradein.model.json.igdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.access.method.P;

import java.util.*;

/**
 * Created by lykav on 7/14/2017.
 */
public class IgdbRelease {
    private int platform;
    private int region;
    private Date date;

    @JsonIgnore
    private static final Map<Integer, String> regionMap;
    static{
        Map<Integer, String> tempRegionMap = new HashMap<>();

        tempRegionMap.put(1, "Europe (EU)");
        tempRegionMap.put(2, "North America (NA)");
        tempRegionMap.put(3, "Australia (AU)");
        tempRegionMap.put(4, "New Zealand (NZ)");
        tempRegionMap.put(5, "Japan (JP)");
        tempRegionMap.put(6, "China (CH)");
        tempRegionMap.put(7, "Asia (AS)");
        tempRegionMap.put(8, "Worldwide");

        regionMap = Collections.unmodifiableMap(tempRegionMap);
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @JsonIgnore
    public String getRegionName(){
        String regionName = regionMap.get(this.region);
        if(regionName != null){
            return regionName;
        } else{
            // no matching region name found, return null
            return null;
        }
    }
}
