package controller;

import model.GameEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.GameRepository;

import java.util.List;

/**
 * Created by homepppp on 2017/6/29.
 */

@RestController
public class GameController {

    GameRepository gamerepository;

    //retrieve all games

    @RequestMapping(value="/game/",method= RequestMethod.GET)
    public ResponseEntity<List<GameEntity>> listAllGames(){
        System.out.println("fetch all the games....");
        List<GameEntity> games=gamerepository.findAll();
        if(games.isEmpty()){
            return new ResponseEntity<List<GameEntity>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<GameEntity>>(games,HttpStatus.OK);
    }

    //retrieve single game

    @RequestMapping(value="/game/{gameid}",method=RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value="/game/",method=RequestMethod.POST)
    public ResponseEntity<Void> createGame(@RequestBody GameEntity game, UriComponentsBuilder ucBuilder){
        System.out.println("Create game: "+game.getTitle());
        if(gamerepository.findByTitle(game.getTitle())!=null){
            System.out.println("the game "+game.getTitle()+" is already existed.");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        gamerepository.save(game);
        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(ucBuilder.path("/game/{gameid}").buildAndExpand(game.getGameId()).toUri());
        return new ResponseEntity<Void>(headers,HttpStatus.CREATED);
    }

    //Update a game

    @RequestMapping(value="/game/{gameid}",method=RequestMethod.PUT)
    public ResponseEntity<GameEntity> updateGame(@PathVariable("gameid") int gameid,@RequestBody GameEntity game){
        System.out.println("Update game "+gameid);
        GameEntity currentGame=gamerepository.findOne(gameid);
        if(currentGame==null){
            System.out.println("Game with id "+gameid+"is not existed.");
            return new ResponseEntity<GameEntity>(HttpStatus.NOT_FOUND);
        }
        currentGame.setEvaluatePoint(game.getEvaluatePoint());
        currentGame.setGenre(game.getGenre());
        currentGame.setLanguage(game.getLanguage());
        currentGame.setOffers(game.getOffers());
        currentGame.setPlatform(game.getPlatform());
        currentGame.setTitle(game.getTitle());
        currentGame.setTradeGames(game.getTradeGames());
        currentGame.setWishes(game.getWishes());

        gamerepository.updateGame(currentGame.getTitle(),currentGame.getPlatform(),currentGame.getLanguage(),currentGame.getGenre(),currentGame.getEvaluatePoint(),currentGame.getWishes(),currentGame.getOffers(),currentGame.getTradeGames(),currentGame.getGameId());
        return new ResponseEntity<GameEntity>(currentGame,HttpStatus.OK);
    }

    //Delete a game

    @RequestMapping(value="/game/{gameid}",method=RequestMethod.DELETE)
    public ResponseEntity<GameEntity> deleteGame(@PathVariable ("gameid") int gameid){
        System.out.println("Fetch and delete game with id "+gameid);

        GameEntity game=gamerepository.findOne(gameid);
        if(game==null){
            System.out.println("Unable to delete.Game with id "+gameid+" not found");
            return new ResponseEntity<GameEntity>(HttpStatus.NOT_FOUND);
        }

        gamerepository.delete(gameid);
        return new ResponseEntity<GameEntity>(HttpStatus.NO_CONTENT);
    }
}
