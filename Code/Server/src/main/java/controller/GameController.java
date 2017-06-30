package controller;

import model.GameEntity;
import model.PendingGameEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.GameRepository;
import repository.PendingGameRepository;

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

    //create a game
    /*
    @RequestMapping(value="/",method=RequestMethod.POST)
    public ResponseEntity<Void> createGame(@RequestBody GameEntity game, UriComponentsBuilder ucBuilder){
        System.out.println("Create game: "+game.getTitle());

        if(gamerepository.findByTitle(game.getTitle())!=null){
            System.out.println("the game "+game.getTitle()+" is already existed.");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        if(pendingrepository.findByTitle(game.getTitle())!=null){
            System.out.println("the game "+game.getTitle()+" is being audited");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }


        /*
         * create a new PendingGameEntity
         */
     /*
        PendingGameEntity pendGame=new PendingGameEntity();
        pendGame.setGenre(game.getGenre());
        pendGame.setLanguage(game.getLanguage());

        gamerepository.saveAndFlush(game);
        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(ucBuilder.path("api/game/{gameid}").buildAndExpand(game.getGameId()).toUri());
        return new ResponseEntity<Void>(headers,HttpStatus.CREATED);
    }*/
}
