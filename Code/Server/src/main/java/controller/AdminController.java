package controller;

import model.*;
import model.temporaryItem.PotentialChangesItem;
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
import script.pythonGetEvaluatePoint;

import java.util.*;

/**
 * Created by homepppp on 2017/7/3.
 */
@RestController
@RequestMapping("/api/admin/")
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
    @Autowired
    WishRepository wishRepo;


    //Fetch All Users
    @RequestMapping(value="{adminid}/user",method= RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> ListAllUser(){
        System.out.println("Fetch All Users...");
        List<UserEntity> user=userRepo.findNormalUsers();
        if(user==null){
            System.out.println("can't find any user");
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
            System.out.println("can't find any game");
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
            System.out.println("can't find pendingGame");
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
            System.out.println("can't find the game");
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
            System.out.println("can't find the game.");
            return new ResponseEntity<PendingGameEntity>(game,HttpStatus.NOT_FOUND);
        }
        if(admin==null) {
            System.out.println("can't find the admin.");
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
            System.out.println("can't find the game.");
            return new ResponseEntity<PendingGameEntity>(game,HttpStatus.NOT_FOUND);
        }
        if(admin==null){
            System.out.println("can't find the admin.");
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

        //set the evaluate point
        String point=pythonGetEvaluatePoint.getPoints(game.getTitle(),game.getPlatform());
        System.out.println(point);
        float floatPoint=Float.parseFloat(point)*100;
        newGame.setEvaluatePoint((int)floatPoint);

        gameRepo.saveAndFlush(newGame);
        return new ResponseEntity<PendingGameEntity>(game,HttpStatus.OK);
    }


    //fetch all the orders
    @RequestMapping(value="{adminid}/order",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getAllOrder(@PathVariable("adminid")int adminid){
        System.out.println("fetch all orders...");

        UserEntity user=userRepo.findOne(adminid);
        if(user==null){
            System.out.println("can't find admin...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();

        if(tradeOrderList.isEmpty()){
            System.out.println("can't find any order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

        //set the general detail
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


        //set the information needed
        List<TradeGameEntity> tradeGameList=tradeGameRepo.findAllTradeGame();
        Iterator<TradeGameEntity> iterGame=tradeGameList.iterator();
        while(iterGame.hasNext()){
            TradeGameEntity tradeGame=iterGame.next();
            int orderid=tradeGame.getTradeOrder().getTradeOrderId();
            for(int i =0;i<ShowResult.size();i++){
                if(ShowResult.get(i).getTradeOrderId()==orderid){
                    ShowOrderGamesItem ShowGameItem=new ShowOrderGamesItem();
                    ShowGameItem.setFromAddress(tradeGame.getFromAddress());
                    ShowGameItem.setGameId(tradeGame.getGame().getGameId());
                    ShowGameItem.setReceiver(tradeGame.getReceiver());
                    ShowGameItem.setReceiverStatus(tradeGame.getReceiverStatus());
                    ShowGameItem.setSender(tradeGame.getSender());
                    ShowGameItem.setSenderStatus(tradeGame.getSenderStatus());
                    ShowGameItem.setStatus(tradeGame.getStatus());
                    ShowGameItem.setToAddress(tradeGame.getToAddress());
                    ShowGameItem.setTrackingNumber(tradeGame.getTrackingNumber());
                    ShowGameItem.setTradeGameId(tradeGame.getTradeGameId());

                    List<ShowOrderGamesItem> temp=ShowResult.get(i).getGameDetail();
                    System.out.println(temp.size());
                    temp.add(0,ShowGameItem);
                    ShowResult.get(i).setGameDetail(temp);
                }
            }
        }

        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);
    }


    //get all the available orders
    @RequestMapping(value="{adminid}/change",method = RequestMethod.GET)
    public ResponseEntity<List<PotentialChangesItem>> getAllChanges(@PathVariable("adminid")int adminid){
        System.out.println("get all changes...");

        UserEntity user=userRepo.findOne(adminid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<List<PotentialChangesItem>>(HttpStatus.NOT_FOUND);
        }

        List<PotentialChangesItem> PotentialChangesList=new ArrayList<>();
        List<Object[]> map=wishRepo.getPotientialChanges();

        if(map.isEmpty()){
            System.out.println("can't find any order...");
            return new ResponseEntity<List<PotentialChangesItem>>(HttpStatus.NOT_FOUND);
        }

        //get the information to be shown
        for(Object[] row:map){
            PotentialChangesItem orderItem=new PotentialChangesItem();
            Object[] cells=(Object[]) row;
            orderItem.setUserAId((int)cells[0]);
            orderItem.setUserBId((int)cells[2]);
            orderItem.setUserASendGameId((int)cells[1]);
            orderItem.setUserBSendGameId((int)cells[3]);
            PotentialChangesList.add(orderItem);
        }
        return new ResponseEntity<List<PotentialChangesItem>>(PotentialChangesList,HttpStatus.OK);
    }
}
