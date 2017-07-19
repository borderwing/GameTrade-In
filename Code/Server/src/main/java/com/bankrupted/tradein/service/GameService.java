package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.json.game.GameDetailJson;
import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbRelease;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by lykav on 7/18/2017.
 */
@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    IgdbUtility igdbUtility;
    @Autowired
    GameAsyncService gameAsyncService;


//    public GameTileJson getGameTile(Long igdbId){
//
//    }

    public List<GameTileJson> getTrendingGameTileList(int limit, int offset) {
        List<IgdbGame> igdbGames = igdbUtility.getTrendingIgdbGames(limit, offset);

        List<CompletableFuture<GameTileJson>> gameTiles = new ArrayList<>(igdbGames.size());

        try {
            for (IgdbGame game : igdbGames) {
                gameTiles.add(gameAsyncService.convertToGameTile(game));
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        List<GameTileJson> completed = allOf(gameTiles).join();

        return completed;
    }

    public GameDetailJson getIgdbGame(Long igdbId){
        IgdbGame igdbGame = igdbUtility.getIgdbGame(igdbId);

        // ------ fill in the platform names for the game ------
        // prepare platformIdSet
        Set<Integer> platformIdSet = new HashSet<>();
        if(igdbGame.getReleaseDates() != null){
            for(IgdbRelease release : igdbGame.getReleaseDates()){
                ;
            }
        }

        return null;
    }

    private <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        return allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }



}