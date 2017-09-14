package com.example.ye.gametrade_in.Bean.utils;

/**
 * Created by lykav on 9/11/2017.
 */

public class RegionBean {
    private int regionId;
    private String region;

    public RegionBean(int regionId, String region) {
        this.regionId = regionId;
        this.region = region;
    }

    public int getRegionId() {
        return regionId;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionBean that = (RegionBean) o;

        return regionId == that.regionId;

    }

    @Override
    public int hashCode() {
        return regionId;
    }
}
