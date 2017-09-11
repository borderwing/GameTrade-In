package com.example.ye.gametrade_in.utils;

import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameReleaseJson;
import com.example.ye.gametrade_in.Bean.utils.PlatformBean;
import com.example.ye.gametrade_in.Bean.utils.RegionBean;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Platform;

/**
 * Created by lykav on 9/11/2017.
 */

public class GameDetailUtility {
    private GameDetailBean mGameDetailBean;

    MultiValuedMap<PlatformBean, RegionBean> mPlatformToRegionMap
            = new HashSetValuedHashMap<>();

    Map<Integer, PlatformBean> mPlatformMap = new HashedMap<>();
    Map<Integer, RegionBean> mRegionMap = new HashedMap<>();

    public GameDetailUtility(GameDetailBean gameDetailBean){
        this.mGameDetailBean = gameDetailBean;

        for(GameReleaseJson release : mGameDetailBean.getReleases()){
            PlatformBean platform =
                    new PlatformBean(release.getPlatformId(), release.getPlatform());
            RegionBean region =
                    new RegionBean(release.getRegionId(), release.getRegion());

            mPlatformToRegionMap.put(platform, region);
            mPlatformMap.put(platform.getPlatformId(), platform);
            mRegionMap.put(region.getRegionId(), region);
        }

    }

    public List<PlatformBean> getPlatforms(){
        List<PlatformBean> list = new ArrayList<>();
        list.addAll(mPlatformMap.values());

        return list;
    }

    public List<RegionBean> getRegionsWithPlatform(PlatformBean platformBean){
        List<RegionBean> list = new ArrayList<>();
        list.addAll(mPlatformToRegionMap.get(platformBean));

        return list;
    }



}
