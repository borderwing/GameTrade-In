package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.json.game.GameDetailJson;
import com.bankrupted.tradein.model.json.game.GameTileJson;
import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.service.GameService;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.bankrupted.tradein.repository.GameRepository;
import com.bankrupted.tradein.repository.PendingGameRepository;

import java.util.List;

/**
 * Created by homepppp on 2017/6/29.
 */
@EnableAsync
@RestController
@RequestMapping(value="/api/game")
public class GameController {

    @Autowired
    GameRepository gamerepository;
    @Autowired
    PendingGameRepository pendingrepository;


    @Autowired
    GameService gameService;


    //retrieve all games
    @RequestMapping(value = "/params", method = RequestMethod.GET)
    public ResponseEntity<List<GameEntity>> listAllGames(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = new PageRequest(page, size);
        List<GameEntity> allGame = gamerepository.findAll();

        PagedListHolder<GameEntity> pagedAllGame = new PagedListHolder<>(allGame);
        pagedAllGame.setPage(page);
        pagedAllGame.setPageSize(size);

        return new ResponseEntity<List<GameEntity>>(pagedAllGame.getPageList(), HttpStatus.OK);
    }

    // retrieve trending games
    @RequestMapping(value="/trending", method=RequestMethod.GET)
    public ResponseEntity<List<GameTileJson>> getTrendingGames(
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer page)
    {
        if(size <= 0 || page < 0) {
            return new ResponseEntity<List<GameTileJson>>(HttpStatus.BAD_REQUEST);
        }

        List<GameTileJson> trendingGames = gameService.getTrendingGameTileList(size, page * size);

        if(trendingGames == null){
            System.out.println("Fetch trending games failed");
            return new ResponseEntity<List<GameTileJson>>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<List<GameTileJson>>(trendingGames, HttpStatus.OK);
    }

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public ResponseEntity<List<GameTileJson>> getSearchResults(
            @RequestParam(value = "keyword", required = true) String keyword,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer page)
    {
        if(size <= 0 || page < 0) {
            return new ResponseEntity<List<GameTileJson>>(HttpStatus.BAD_REQUEST);
        }

        List<GameTileJson> searchedGames = gameService.getSearchedGameTileList(keyword, size,page * size);

        if(searchedGames == null){
            System.out.println("Fetch trending games failed");
            return new ResponseEntity<List<GameTileJson>>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<List<GameTileJson>>(searchedGames, HttpStatus.OK);
    }


    //retrieve single game

//    @RequestMapping(value="/{gameid}",method=RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<GameEntity> getGame(@PathVariable("gameid") long gameid){
//        System.out.println("Fetch game with id "+gameid);
//        GameEntity game=gamerepository.findOne(gameid);
//        if(game==null){
//            System.out.println("Games with id "+gameid+" not found");
//            return new ResponseEntity<GameEntity>(game,HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<GameEntity>(game,HttpStatus.OK);
//    }

    @RequestMapping(value = "/{igdbId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameDetailJson> getGame(@PathVariable("igdbId") long igdbId){
        System.out.println("Fetch game with IGDB id "+ igdbId);

        GameDetailJson game = gameService.getIgdbGame(igdbId);
        if(game == null){
            System.out.println("Games with id "+ igdbId +" not found");
            return new ResponseEntity<GameDetailJson>(game, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<GameDetailJson>(game,HttpStatus.OK);
    }

    @RequestMapping(value = "/{igdbId}/evaluate",method=RequestMethod.GET)
    public ResponseEntity<Integer> getEvaluatePoint(@RequestParam(value = "platformId",required = true)int platformId,
                                                    @RequestParam(value = "regionId",required = true)int regionId,
                                                    @PathVariable(value="igdbId")Long igdbId){
        GameEntity game1= gameService.getGameNonBlocked(igdbId,platformId,regionId);

        if(game1.getEvaluatePoint() > 0){
            return new ResponseEntity<Integer>(game1.getEvaluatePoint(),HttpStatus.OK);
        }

        int point = gameService.getPointBlocked(igdbId, platformId,regionId);
        return new ResponseEntity<Integer>(point,HttpStatus.OK);
    }
    //find game by key words
   /* @RequestMapping(value="/params",method=RequestMethod.POST)
    public ResponseEntity<Page<GameEntity>> searchGame(@RequestBody SearchGameJsonItem GameInfo,
                                                       @RequestParam(value="page",defaultValue = "0")Integer page,
                                                       @RequestParam(value="size",defaultValue = "5")Integer size){
        System.out.println("search game...");

        String title,language,genre,platform;

        title=GameInfo.getTitle().equals("...")?"%":"%"+GameInfo.getTitle()+"%";
        language=GameInfo.getLanguage().equals("...")?"%":GameInfo.getLanguage();
        genre=GameInfo.getGenre().equals("...")?"%":GameInfo.getGenre();
        platform=GameInfo.getPlatform().equals("...")?"%":GameInfo.getPlatform();
        System.out.println("title="+title+"language="+language+"genre="+genre+"platform="+platform);

        Pageable pageable=new PageRequest(page,size);
        Page<GameEntity> gameList=gamerepository.Search(pageable,title,language,genre,platform);


        if(gameList==null){
            System.out.println("can't find game...");
            return new ResponseEntity<Page<GameEntity>>(HttpStatus.NOT_FOUND);
        }

        if(gameList.getTotalPages()<(page+1)){
            System.out.println("can't find game in the page...");
            return new ResponseEntity<Page<GameEntity>>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Page<GameEntity>>(gameList,HttpStatus.OK);
    }
*/
}
