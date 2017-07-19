package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.bankrupted.tradein.model.json.igdb.IgdbRelease;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lykav on 7/19/2017.
 */
@Service
public class GameAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(GameAsyncService.class);

    @Autowired
    IgdbUtility igdbUtility;

    @Async
    public CompletableFuture<GameTileJson> convertToGameTile(IgdbGame igdbGame) throws InterruptedException {
        logger.info("Looking up " + igdbGame.getName());

        List<String> gamePlatformNames = this.getPlatformNameList(igdbGame);

        GameTileJson gameTile = new GameTileJson();
        gameTile.setTitle(igdbGame.getName());
        if(igdbGame.getCover() != null) gameTile.setCoverUrl(igdbGame.getCover().getUrlBySize("cover_big_2x"));
        gameTile.setIgdbId(igdbGame.getId());
        gameTile.setPlatforms(gamePlatformNames);
        gameTile.setPopularity(igdbGame.getPopularity());

        return CompletableFuture.completedFuture(gameTile);
    }

    private List<String> getPlatformNameList(IgdbGame igdbGame){
        logger.info("Getting platform name list for: " + igdbGame.getName());

        Set<Integer> platformSet = new HashSet<>();

        if(igdbGame.getReleaseDates() == null){
            return new ArrayList<String>();
        }

        for(IgdbRelease release : igdbGame.getReleaseDates()){
            platformSet.add(release.getPlatform());
        }

        List<Integer> platformList = new ArrayList<>();
        platformList.addAll(platformSet);

        List<IgdbPlatform> platforms = igdbUtility.getIgdbPlatforms(platformList);

        List<String> platformNameList = new ArrayList<>(platforms.size());
        for(IgdbPlatform platform : platforms){
            platformNameList.add(platform.getName());
        }
        return platformNameList;
    }
}
