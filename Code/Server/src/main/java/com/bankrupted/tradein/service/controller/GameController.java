package com.bankrupted.tradein.service.controller;

import com.bankrupted.tradein.service.model.GameEntity;
import com.bankrupted.tradein.service.model.json.SearchGameJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @RequestMapping(value="/params",method=RequestMethod.GET)
    public ResponseEntity<Page<GameEntity>> listAllGames(@RequestParam(value="page",defaultValue="0")Integer page,
                                                         @RequestParam(value="size",defaultValue="5")Integer size){
        Pageable pageable=new PageRequest(page,size);
        Page<GameEntity> allGame=gamerepository.findAll(pageable);
        return new ResponseEntity<Page<GameEntity>>(allGame,HttpStatus.OK);
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
    @RequestMapping(value="/params",method=RequestMethod.POST)
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

}
