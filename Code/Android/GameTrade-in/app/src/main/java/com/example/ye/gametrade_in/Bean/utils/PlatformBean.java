package com.example.ye.gametrade_in.Bean.utils;

/**
 * Created by lykav on 9/11/2017.
 */

public class PlatformBean {
    private int platformId;
    private String platform;

    public PlatformBean(int platformId, String platform) {
        this.platformId = platformId;
        this.platform = platform;
    }

    public int getPlatformId() {
        return platformId;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformBean that = (PlatformBean) o;

        return platformId == that.platformId;

    }

    @Override
    public int hashCode() {
        return platformId;
    }
}
