package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.bankrupted.tradein.model.json.igdb.IgdbRelease;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbGenre;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbKeyword;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbTheme;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Async
    public CompletableFuture<HashMap<Integer, String>> getFuturePlatformMap(Collection<IgdbRelease> releases){
        Set<Integer> platformIds = preparePlatformIdSet(releases);

        HashMap<Integer,String> platformMap = new HashMap<>();
        if(releases == null || releases.size() == 0){
            return CompletableFuture.completedFuture(platformMap);
        }

        List<IgdbPlatform> igdbPlatformList = igdbUtility.getIgdbPlatforms(platformIds);

        if(igdbPlatformList != null && igdbPlatformList.size() > 0){
            for(IgdbPlatform platform : igdbPlatformList){
                if(platform.getName() != null && platform.getName() != ""){
                    platformMap.put(platform.getId(), platform.getName());
                }
            }
        }
        return CompletableFuture.completedFuture(platformMap);
    }

    public HashMap<Integer, String> getPlatformMap(Set<Integer> platformIdSet){
        HashMap<Integer,String> platformMap = new HashMap<>();
        if(platformIdSet == null || platformIdSet.size() == 0){
            return platformMap;
        }

        List<IgdbPlatform> igdbPlatformList = igdbUtility.getIgdbPlatforms(platformIdSet);

        if(igdbPlatformList != null && igdbPlatformList.size() > 0){
            for(IgdbPlatform platform : igdbPlatformList){
                if(platform.getName() != null && platform.getName() != ""){
                    platformMap.put(platform.getId(), platform.getName());
                }
            }
        }
        return platformMap;
    }

    @Async
    public CompletableFuture<HashMap<Long, String>> getFutureGenreMap(Collection<Long> genreIds){
        Set<Long> genreIdSet = prepareLongSet(genreIds);

        HashMap<Long, String> genreMap = new HashMap<>();
        if(genreIdSet == null || genreIdSet.size() == 0){
            return CompletableFuture.completedFuture(genreMap);
        }

        List<IgdbGenre> igdbGenreList = igdbUtility.getIgdbGenres(genreIdSet);

        if(igdbGenreList != null && igdbGenreList.size() > 0){
            for(IgdbGenre genre : igdbGenreList){
                if(genre.getName() != null && genre.getName() != ""){
                    genreMap.put(genre.getId(), genre.getName());
                }
            }
        }
        return CompletableFuture.completedFuture(genreMap);
    }

    @Async
    public CompletableFuture<HashMap<Long, String>> getFutureThemeMap(Collection<Long> themeIds){
        Set<Long> themeIdSet = prepareLongSet(themeIds);

        HashMap<Long, String> themeMap = new HashMap<>();
        if(themeIdSet == null || themeIdSet.size() == 0){
            return CompletableFuture.completedFuture(themeMap);
        }

        List<IgdbTheme> igdbThemeList = igdbUtility.getIgdbThemes(themeIdSet);


        if(igdbThemeList != null && igdbThemeList.size() > 0){
            for(IgdbTheme theme : igdbThemeList){
                if(theme.getName() != null && theme.getName() != ""){
                    themeMap.put(theme.getId(), theme.getName());
                }
            }
        }
        return CompletableFuture.completedFuture(themeMap);
    }

    @Async
    public CompletableFuture<HashMap<Long, String>> getFutureKeywordMap(Collection<Long> keywordIds){
        Set<Long> keywordIdSet = prepareLongSet(keywordIds);

        HashMap<Long, String> keywordMap = new HashMap<>();
        if(keywordIdSet == null || keywordIdSet.size() == 0){
            return CompletableFuture.completedFuture(keywordMap);
        }

        List<IgdbKeyword> igdbKeywordList = igdbUtility.getIgdbKeywords(keywordIdSet);

        if(igdbKeywordList != null && igdbKeywordList.size() > 0){
            for(IgdbKeyword keyword : igdbKeywordList){
                if(keyword.getName() != null && keyword.getName() != ""){
                    keywordMap.put(keyword.getId(), keyword.getName());
                }
            }
        }
        return CompletableFuture.completedFuture(keywordMap);
    }


    protected Set<Integer> preparePlatformIdSet(Collection<IgdbRelease> releases) {
        if(releases == null){
            return new HashSet<>();
        } else{
            Set<Integer> hashSet = new HashSet<>();
            for(IgdbRelease release : releases){
                hashSet.add(release.getPlatform());
            }
            return hashSet;
        }
    }

    protected Set<Long> prepareLongSet(Collection<Long> idList){
        if(idList == null){
            return new HashSet<>();
        } else {
            Set<Long> hashSet = new HashSet<>();
            hashSet.addAll(idList);
            return hashSet;
        }
    }

    protected List<String> getPlatformNameList(IgdbGame igdbGame){
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

        if(platforms == null){
            return null;
        }

        List<String> platformNameList = new ArrayList<>(platforms.size());
        for(IgdbPlatform platform : platforms){
            platformNameList.add(platform.getName());
        }
        return platformNameList;
    }

    protected Set<String> getPlatformNameSet(IgdbGame igdbGame, Map<Integer, String> platformMap){
        if(igdbGame == null || platformMap == null){
            return null;
        }
        Set<String> platformNameSet = new HashSet<>();
        if(igdbGame.getReleaseDates() != null){
            for(IgdbRelease release : igdbGame.getReleaseDates()){
                String name = platformMap.get(release.getPlatform());
                if(name != null){
                    platformNameSet.add(name);
                }
            }
        }
        return platformNameSet;
    }
}
