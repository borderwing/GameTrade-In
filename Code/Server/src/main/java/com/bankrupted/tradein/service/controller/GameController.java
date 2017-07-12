package com.bankrupted.tradein.service.controller;

import com.bankrupted.tradein.service.model.GameEntity;
import com.bankrupted.tradein.service.model.json.SearchGameJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankrupted.tradein.service.repository.GameRepository;
import com.bankrupted.tradein.service.repository.PendingGameRepository;

import java.util.List;

/**
 * Created by homepppp on 2017/6/29.
 */

@RestController
@RequestMapping(value="/api/game")
public class GameController {

    @Autowired
    GameRepository gamerepository;
    @Autowired
    PendingGameRepository pendingrepository;

    //retrieve all games

    @RequestMapping(value="/",method= RequestMethod.GET)
    public ResponseEntity<List<GameEntity>> listAllGames(){
        System.out.println("fetch all the games....");
        List<GameEntity> games=gamerepository.findAll();
        if(games.isEmpty()){
            return new ResponseEntity<List<GameEntity>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<GameEntity>>(games,HttpStatus.OK);
    }

    //retrieve single game

    @RequestMapping(value="/{gameid}",method=RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameEntity> getGame(@PathVariable("gameid") int gameid){
        System.out.println("Fetch game with id "+gameid);
        GameEntity game=gamerepository.findOne(gameid);
        if(game==null){
            System.out.println("Games with id "+gameid+" not found");
            return new ResponseEntity<GameEntity>(game,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<GameEntity>(game,HttpStatus.OK);
    }


    //find game by key words
    @RequestMapping(value="/",method=RequestMethod.POST)
    public ResponseEntity<List<GameEntity>> searchGame(@RequestBody SearchGameJsonItem GameInfo){
        System.out.println("search game...");

        String title,language,genre,platform;

        title=GameInfo.getTitle().equals("...")?"%":"%"+GameInfo.getTitle()+"%";
        language=GameInfo.getLanguage().equals("...")?"%":GameInfo.getLanguage();
        genre=GameInfo.getGenre().equals("...")?"%":GameInfo.getGenre();
        platform=GameInfo.getPlatform().equals("...")?"%":GameInfo.getPlatform();
        System.out.println("title="+title+"language="+language+"genre="+genre+"platform="+platform);

        List<GameEntity> gameList=gamerepository.Search(title,language,genre,platform);


        if(gameList.isEmpty()){
            System.out.println("can't find game...");
            return new ResponseEntity<List<GameEntity>>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<GameEntity>>(gameList,HttpStatus.OK);
    }

}
