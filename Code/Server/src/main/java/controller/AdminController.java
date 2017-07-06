package controller;

import model.*;
import model.temporaryItem.ShowOrderGamesItem;
import model.temporaryItem.ShowOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import repository.*;

import java.util.ArrayList;
import java.util.Iterator;
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
    @Autowired
    TradeOrderRepository tradeOrderRepo;
    @Autowired
    TradeGameRepository tradeGameRepo;


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


    //fetch all the orders
    @RequestMapping(value="{adminid}/order",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getAllOrder(@PathVariable("adminid")int adminid){
        System.out.println("fetch all orders...");

        UserEntity user=userRepo.findOne(adminid);
        if(user==null){
            System.out.println("cant find admin...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();

        if(tradeOrderList.isEmpty()){
            System.out.println("cant find any order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

        List<ShowOrderItem> ShowResult=new ArrayList<>();
        Iterator<TradeOrderEntity> iter=tradeOrderList.iterator();
        while(iter.hasNext()){
            TradeOrderEntity order=iter.next();
            ShowOrderItem showItem=new ShowOrderItem();
            showItem.setStatus(order.getStatus());
            showItem.setTime(order.getCreatetime());
            showItem.setTradeOrderId(order.getTradeOrderId());
            ShowResult.add(showItem);
        }

        System.out.println("---------------------------------------------------------------");
        System.out.println("here0");
        System.out.println("---------------------------------------------------------------");
        List<TradeGameEntity> tradeGameList=tradeGameRepo.findAllTradeGame();
        System.out.println("---------------------------------------------------------------");
        System.out.println("here1");
        System.out.println("---------------------------------------------------------------");
        Iterator<TradeGameEntity> iterGame=tradeGameList.iterator();
        while(iterGame.hasNext()){
            TradeGameEntity tradeGame=iterGame.next();
            int orderid=tradeGame.getTradeOrder().getTradeOrderId();
            for(int i =0;i<ShowResult.size();i++){
                System.out.println("---------------------------------------------------------------");
                System.out.println(ShowResult.size()+"       "+orderid);
                System.out.println("here"+i);
                System.out.println("---------------------------------------------------------------");
                if(ShowResult.get(i).getTradeOrderId()==orderid){
                    System.out.println("here0");
                    ShowOrderGamesItem ShowGameItem=new ShowOrderGamesItem();
                    System.out.println("here1");
                    ShowGameItem.setFromAddress(tradeGame.getFromAddress());
                    System.out.println("here2");
                    ShowGameItem.setGameId(tradeGame.getGame().getGameId());
                    System.out.println("here3");
                    ShowGameItem.setReceiver(tradeGame.getReceiver());
                    System.out.println("here4");
                    ShowGameItem.setReceiverStatus(tradeGame.getReceiverStatus());
                    System.out.println("here5");
                    ShowGameItem.setSender(tradeGame.getSender());
                    System.out.println("here6");
                    ShowGameItem.setSenderStatus(tradeGame.getSenderStatus());
                    System.out.println("here7");
                    ShowGameItem.setStatus(tradeGame.getStatus());
                    System.out.println("here8");
                    ShowGameItem.setToAddress(tradeGame.getToAddress());
                    System.out.println("here9");
                    ShowGameItem.setTrackingNumber(tradeGame.getTrackingNumber());
                    System.out.println("here10");
                    ShowGameItem.setTradeGameId(tradeGame.getTradeGameId());
                    System.out.println("here16");

                    List<ShowOrderGamesItem> temp=ShowResult.get(i).getGameDetail();
                    System.out.println("here11");
                    System.out.println(temp.size());
                    System.out.println("here11");
                    temp.add(0,ShowGameItem);
                    System.out.println("here13");
                    ShowResult.get(i).setGameDetail(temp);
                    System.out.println("here14");
                }
            }
        }

        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);
    }

}
