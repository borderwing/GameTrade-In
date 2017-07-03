package controller;

import model.GameEntity;
import model.PendingGameEntity;
import model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import repository.GameRepository;
import repository.PendingGameRepository;
import repository.UserRepository;

import java.util.List;

/**
 * Created by homepppp on 2017/7/3.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    GameRepository gameRepo;
    @Autowired
    PendingGameRepository pendingGameRepo;


    //Fetch All Users
    @RequestMapping(value="{adminid}/user",method= RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> ListAllUser(){
        System.out.println("Fetch All Users...");
        List<UserEntity> user=userRepo.findNormalUsers();
        if(user==null){
            System.out.println("cant find any user");
            return new ResponseEntity<List<UserEntity>>(user,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<UserEntity>>(user,HttpStatus.OK);
    }


    //Fetch All Games
    @RequestMapping(value="{adminid}/game",method=RequestMethod.GET)
    public ResponseEntity<List<GameEntity>> ListAllGame(){
        System.out.println("Fetch All Games...");
        List<GameEntity> game=gameRepo.findAll();
        if(game==null){
            System.out.println("cant find any game");
            return new ResponseEntity<List<GameEntity>>(game,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<GameEntity>>(game,HttpStatus.OK);
    }

    //Fetch All PendingGames
    @RequestMapping(value="{adminid}/pendingGame",method=RequestMethod.GET)
    public ResponseEntity<List<PendingGameEntity>> ListAllPendingGame(){
        System.out.println("Fetch All PendingGame...");
        List<PendingGameEntity> pendingGame=pendingGameRepo.findNoReviewerPendingGame();
        if(pendingGame==null){
            System.out.println("cant find pendingGame");
            return new ResponseEntity<List<PendingGameEntity>>(pendingGame,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<PendingGameEntity>>(pendingGame,HttpStatus.OK);
    }


    //Fetch pending game by ID
    @RequestMapping(value="{adminid}/pendingGame/{pendingGameid}",method=RequestMethod.GET)
    public ResponseEntity<PendingGameEntity> getPendingGame(@PathVariable("pendingGameid")int pendingGameId){
        System.out.println("Fetch pending game...");
        PendingGameEntity game=pendingGameRepo.findOne(pendingGameId);
        if(game==null){
            System.out.println("cant find the game");
            return new ResponseEntity<PendingGameEntity>(game,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<PendingGameEntity>(game,HttpStatus.OK);
    }

    //failed in pending
    @RequestMapping(value="{adminid}/pendingGame/{pendingGameid}/failure",method=RequestMethod.PUT)
    public ResponseEntity<PendingGameEntity> failurePendingGame(
            @PathVariable("adminid")int adminId,
            @PathVariable("pendingGameid")int pendingGameId){
        System.out.println("setting the status...");
        PendingGameEntity game=pendingGameRepo.findOne(pendingGameId);
        UserEntity admin=userRepo.findOne(adminId);
        if(game==null){
            System.out.println("cant find the game.");
            return new ResponseEntity<PendingGameEntity>(game,HttpStatus.NOT_FOUND);
        }
        if(admin==null) {
            System.out.println("cant find the admin.");
            return new ResponseEntity<PendingGameEntity>(HttpStatus.NOT_FOUND);
        }
        //set the status to 2 and set the reviewer
        pendingGameRepo.SetReviewer(pendingGameId,admin);
        pendingGameRepo.pendingFailure(pendingGameId);
        return new ResponseEntity<PendingGameEntity>(game,HttpStatus.OK);
    }

    //success in pending
    @RequestMapping(value="{adminid}/pendingGame/{pendingGameid}/success",method=RequestMethod.PUT)
    public ResponseEntity<PendingGameEntity> successPendingGame(
            @PathVariable("adminid")int adminId,
            @PathVariable("pendingGameid")int pendingGameId){
        System.out.println("setting the status...");
        PendingGameEntity game=pendingGameRepo.findOne(pendingGameId);
        UserEntity admin=userRepo.findOne(adminId);
        if(game==null){
            System.out.println("cant find the game.");
            return new ResponseEntity<PendingGameEntity>(game,HttpStatus.NOT_FOUND);
        }
        if(admin==null){
            System.out.println("cant find the admin.");
            return new ResponseEntity<PendingGameEntity>(HttpStatus.NOT_FOUND);
        }
        //set the status to 1 and set the reviewer
        pendingGameRepo.SetReviewer(pendingGameId,admin);
        pendingGameRepo.pendingSuccess(pendingGameId);

        //add to the GameEntity
        GameEntity newGame=new GameEntity();
        newGame.setTitle(game.getTitle());
        newGame.setPlatform(game.getPlatform());
        newGame.setLanguage(game.getLanguage());
        newGame.setGenre(game.getGenre());

        gameRepo.saveAndFlush(newGame);
        return new ResponseEntity<PendingGameEntity>(game,HttpStatus.OK);
    }
}
