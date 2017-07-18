package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbGenre;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.bankrupted.tradein.model.json.igdb.IgdbRelease;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lykav on 7/18/2017.
 */
@Service
public class GameService {
    @Autowired
    IgdbUtility igdbUtility;


//    public GameTileJson getGameTile(Long igdbId){
//
//    }

    public List<GameTileJson> getTrendingGameTileList(int limit, int offset){
        List<IgdbGame> igdbGames = igdbUtility.getTrendingIgdbGames(limit, offset);

        List<GameTileJson> gameTiles = new ArrayList<>(igdbGames.size());
        for(IgdbGame game : igdbGames){
            gameTiles.add(convertToGameTile(game));
        }

        return gameTiles;
    }


    private List<String> getPlatformNameList(IgdbGame igdbGame){

        Set<Integer> platformSet = new HashSet<>();
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

    private GameTileJson convertToGameTile(IgdbGame igdbGame){
        List<String> gamePlatformNames = this.getPlatformNameList(igdbGame);

        GameTileJson gameTile = new GameTileJson();
        gameTile.setTitle(igdbGame.getName());
        gameTile.setCoverUrl(igdbGame.getCover().getUrl());
        gameTile.setIgdbId(igdbGame.getId());
        gameTile.setPlatforms(gamePlatformNames);
        gameTile.setPopularity(igdbGame.getPopularity());

        return gameTile;
    }
}