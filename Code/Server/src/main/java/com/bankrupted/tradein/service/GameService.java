package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.json.game.GameDetailJson;
import com.bankrupted.tradein.model.json.game.GameReleaseJson;
import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbImage;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.bankrupted.tradein.model.json.igdb.IgdbRelease;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbGenre;
import com.bankrupted.tradein.repository.GameRepository;
import com.bankrupted.tradein.script.pythonGetEvaluatePoint;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by lykav on 7/18/2017.
 */
@Service
public class GameService {

    @Autowired
    private pythonGetEvaluatePoint pythonGetEvaluatePoint;

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    IgdbUtility igdbUtility;
    @Autowired
    GameAsyncService gameAsyncService;
    @Autowired
    GameRepository gameRepo;
    @Autowired
    EvaluateAsyncService evaluateAsyncService;

    public List<GameTileJson> getTrendingGameTileList(int limit, int offset) {
        List<IgdbGame> igdbGames = igdbUtility.getTrendingIgdbGames(limit, offset);

        if(igdbGames == null){
            return null;
        }

//        List<CompletableFuture<GameTileJson>> gameTiles = new ArrayList<>(igdbGames.size());
//
//        try {
//            for (IgdbGame game : igdbGames) {
//                gameTiles.add(gameAsyncService.convertToGameTile(game));
//            }
//        } catch (InterruptedException e){
//            e.printStackTrace();
//        }
//
//        List<GameTileJson> completed = allOf(gameTiles).join();

        return convertToGameTiles(igdbGames);
    }

    public List<GameTileJson> getSearchedGameTileList(String keyword, int limit, int offset){
        List<IgdbGame> igdbGames = igdbUtility.getSearchedIgdbGames(keyword, limit, offset);

        if(igdbGames == null){
            return null;
        }
        return convertToGameTiles(igdbGames);
    }

    public GameDetailJson getIgdbGame(Long igdbId){
        IgdbGame igdbGame = igdbUtility.getIgdbGame(igdbId);

        if(igdbGame == null){
            return null;
        }

        CompletableFuture<HashMap<Integer, String>> futurePlatformMap =
                gameAsyncService.getFuturePlatformMap(igdbGame.getReleaseDates());
        CompletableFuture<HashMap<Long, String>> futureGenreMap =
                gameAsyncService.getFutureGenreMap(igdbGame.getGenres());
        CompletableFuture<HashMap<Long, String>> futureThemeMap =
                gameAsyncService.getFutureThemeMap(igdbGame.getThemes());
        CompletableFuture<HashMap<Long, String>> futureKeywordMap =
                gameAsyncService.getFutureKeywordMap(igdbGame.getKeywords());

        CompletableFuture.allOf(futurePlatformMap, futureGenreMap, futureThemeMap, futureKeywordMap).join();

        HashMap<Integer, String> platformMap = futurePlatformMap.join();
        HashMap<Long, String> genreMap = futureGenreMap.join();
        HashMap<Long, String> themeMap = futureThemeMap.join();
        HashMap<Long, String> keywordMap = futureKeywordMap.join();



        GameDetailJson gameDetail = new GameDetailJson();
        gameDetail.setIgdbId(igdbGame.getId());
        gameDetail.setTitle(igdbGame.getName());
        gameDetail.setPopularity(igdbGame.getPopularity());
        gameDetail.setSummary(igdbGame.getSummary());

        List<GameReleaseJson> gameReleases;
        if(igdbGame.getReleaseDates() != null){
            gameReleases = new ArrayList<>(igdbGame.getReleaseDates().size());
            for(IgdbRelease igdbRelease : igdbGame.getReleaseDates()){
                String platformName = platformMap.get(igdbRelease.getPlatform());
                if(platformName == null)  continue;

                GameReleaseJson gameRelease = new GameReleaseJson();
                gameRelease.setPlatformId(igdbRelease.getPlatform());
                gameRelease.setPlatform(platformName);
                gameRelease.setRegionId(igdbRelease.getRegion());
                gameRelease.setRegion(igdbRelease.getRegionName());
                gameReleases.add(gameRelease);
            }
            gameDetail.setReleases(gameReleases);
        }

        if(igdbGame.getCover() != null) gameDetail.setCoverUrl(igdbGame.getCover().getUrlBySize("cover_big_2x"));
        if(igdbGame.getScreenshots() != null){
            List<String> screenshots = new ArrayList<>(igdbGame.getScreenshots().size());
            for(IgdbImage image : igdbGame.getScreenshots()){
                screenshots.add(image.getUrlBySize("screenshot_med"));
            }
            gameDetail.setScreenshots(screenshots);
        }

        gameDetail.setGenres(new ArrayList<>(genreMap.values()));
        gameDetail.setThemes(new ArrayList<>(themeMap.values()));
        gameDetail.setKeywords(new ArrayList<>(keywordMap.values()));

        gameDetail.setUrl(igdbGame.getUrl());

        return gameDetail;
    }

    private  List<GameTileJson> convertToGameTiles(Collection<IgdbGame> igdbGames){
        List<GameTileJson> gameTiles = new LinkedList<>();

        if(igdbGames == null || igdbGames.size() == 0){
            return gameTiles;
        }

        Set<Integer> platformIdSet = new HashSet<>();
        for(IgdbGame igdbGame : igdbGames){
            platformIdSet.addAll(gameAsyncService.preparePlatformIdSet(igdbGame.getReleaseDates()));
        }

        HashMap<Integer, String> platformMap = new HashMap<>();

        try {
            platformMap = gameAsyncService.getPlatformMap(platformIdSet);
        } catch (Exception e){
            e.printStackTrace();
        }

        for(IgdbGame igdbGame : igdbGames) {
            List<String> gamePlatformNames = new ArrayList<>();
            gamePlatformNames.addAll(gameAsyncService.getPlatformNameSet(igdbGame, platformMap));

            GameTileJson gameTile = new GameTileJson();
            gameTile.setTitle(igdbGame.getName());
            if (igdbGame.getCover() != null) gameTile.setCoverUrl(igdbGame.getCover().getUrlBySize("cover_big"));
            gameTile.setIgdbId(igdbGame.getId());
            gameTile.setPlatforms(gamePlatformNames);
            gameTile.setPopularity(igdbGame.getPopularity());
            gameTile.setSummary(igdbGame.getSummary());

            gameTiles.add(gameTile);
        }

        return gameTiles;
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


    //game Repository operation
    public GameEntity fetchOneGame(Long gameid){
        return gameRepo.findOne(gameid);
    }


    @Transactional
    public GameEntity getGameNonBlocked(Long igdbId,int platformId,int regionId){
        GameEntity game = gameRepo.getGame(igdbId,platformId,regionId);
        if(game != null) {
            return game;
        } else {
            addGameNonBlocked(igdbId, platformId, regionId);
            return gameRepo.getGame(igdbId, platformId, regionId);
        }
    }

    @Transactional
    public GameEntity getGameBlocked(Long igdbId,int platformId,int regionId){
        GameEntity game = gameRepo.getGame(igdbId,platformId,regionId);
        if(game == null) {
            addGameNonBlocked(igdbId, platformId, regionId);
            return gameRepo.getGame(igdbId, platformId, regionId);
        } else if(game.getEvaluatePoint() == 0) {
            GameDetailJson gameDetail=getIgdbGame(igdbId);

            if(gameDetail == null){
                return null;
            }
            String title = gameDetail.getTitle();
            List<GameReleaseJson> gameReleases = gameDetail.getReleases();
            if(gameReleases == null){
                return null;
            }
            String platform=null;
            Iterator<GameReleaseJson> iter = gameReleases.iterator();
            while(iter.hasNext()){
                GameReleaseJson release=iter.next();
                if(release.getPlatformId()==platformId){
                    platform=release.getPlatform();
                    break;
                }
            }
            if(platform == null){
                return null;
            }

            blockedSetPoints(title, platform, igdbId);
            return gameRepo.getGame(igdbId, platformId, regionId);
        } else {
            return game;
        }
    }


    @Transactional
    private boolean addGameNonBlocked(Long igdbId, int platformId, int regionId){
        GameEntity game=new GameEntity();
        game.setIgdbId(igdbId);
        game.setPlatformId(platformId);
        game.setRegionId(regionId);
        GameDetailJson gameDetail=getIgdbGame(igdbId);

        if(gameDetail == null){
            return false;
        }
        String title = gameDetail.getTitle();
        List<GameReleaseJson> gameReleases = gameDetail.getReleases();
        if(gameReleases == null){
            return false;
        }
        String platform=null;
        Iterator<GameReleaseJson> iter = gameReleases.iterator();
        while(iter.hasNext()){
            GameReleaseJson release=iter.next();
            if(release.getPlatformId()==platformId){
                platform=release.getPlatform();
                break;
            }
        }
        if(platform == null){
            return false;
        }

        GameEntity tempGame = gameRepo.getGame(igdbId, platformId, regionId);
        if(tempGame == null) {
            tempGame = gameRepo.saveAndFlush(game);
        }

//        evaluateAsyncService.deferredSetPoints(title, platform, game.getGameId());

        return true;
    }


    @Transactional
    private boolean addGameBlocked(Long igdbId, int platformId, int regionId){
        GameEntity game=new GameEntity();
        game.setIgdbId(igdbId);
        game.setPlatformId(platformId);
        game.setRegionId(regionId);
        GameDetailJson gameDetail=getIgdbGame(igdbId);

        if(gameDetail == null){
            return false;
        }
        String title = gameDetail.getTitle();
        List<GameReleaseJson> gameReleases = gameDetail.getReleases();
        if(gameReleases == null){
            return false;
        }
        String platform=null;
        Iterator<GameReleaseJson> iter = gameReleases.iterator();
        while(iter.hasNext()){
            GameReleaseJson release=iter.next();
            if(release.getPlatformId()==platformId){
                platform=release.getPlatform();
                break;
            }
        }
        if(platform == null){
            return false;
        }

        GameEntity tempGame = gameRepo.getGame(igdbId, platformId, regionId);
        if(tempGame == null) {
            tempGame = gameRepo.saveAndFlush(game);
        }

        this.blockedSetPoints(title, platform, game.getGameId());

        return true;
    }


    void blockedSetPoints(String title, String platform, Long gameId){

        pythonGetEvaluatePoint evaluate = new pythonGetEvaluatePoint();
        String point = evaluate.getPoints(title, platform);


        float floatPoint=Float.parseFloat(point)*100;
        int finalPoint = (int)floatPoint;

        gameRepo.updateGameEvaluatePoint(finalPoint, gameId);
    }



}