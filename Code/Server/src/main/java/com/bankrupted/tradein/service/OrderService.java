package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.*;
import com.bankrupted.tradein.model.json.offer.ConfirmMatchJson;
import com.bankrupted.tradein.model.temporaryItem.ShowOrderGamesItem;
import com.bankrupted.tradein.model.temporaryItem.ShowOrderItem;
import com.bankrupted.tradein.repository.AddressRepository;
import com.bankrupted.tradein.repository.TradeGameRepository;
import com.bankrupted.tradein.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class OrderService {

    @Autowired
    TradeOrderRepository tradeOrderRepo;
    @Autowired
    TradeGameRepository tradeGameRepo;
    @Autowired
    AddressRepository addressRepo;

    public TradeOrderEntity getTradeOrderById(int orderid){
        return tradeOrderRepo.findOne(orderid);
    }

    public List<TradeGameEntity> getAllTradeGames(){
        return tradeGameRepo.findAll();
    }

    public List<TradeGameEntity> getTradeGamesById(int orderid){
        return tradeGameRepo.findByOrderId(orderid);
    }

    public ShowOrderGamesItem getShowGameItem(TradeGameEntity tradeGame){
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

        return ShowGameItem;
    }

    public List<ShowOrderItem> getUnconfirmedGeneralOrder(){
        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();
        List<ShowOrderItem> ShowResult=new ArrayList<>();
        Iterator<TradeOrderEntity> iterTradeOrder=tradeOrderList.iterator();
        while(iterTradeOrder.hasNext()){
            TradeOrderEntity TradeOrder= iterTradeOrder.next();
            if(TradeOrder.getStatus()>0) {
                ShowOrderItem showItem = new ShowOrderItem();
                showItem.setStatus(TradeOrder.getStatus());
                showItem.setTime(TradeOrder.getCreatetime());
                showItem.setTradeOrderId(TradeOrder.getTradeOrderId());
                ShowResult.add(showItem);
            }
        }
        return ShowResult;
    }

    public List<ShowOrderItem> getFinishedGeneralOrder(){
        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();
        List<ShowOrderItem> ShowResult=new ArrayList<>();
        Iterator<TradeOrderEntity> iterTradeOrder=tradeOrderList.iterator();
        while(iterTradeOrder.hasNext()){
            TradeOrderEntity TradeOrder=iterTradeOrder.next();
            if(TradeOrder.getStatus()==0){
                ShowOrderItem showItem=new ShowOrderItem();
                showItem.setStatus(0);
                showItem.setTime(TradeOrder.getCreatetime());
                showItem.setTradeOrderId(TradeOrder.getTradeOrderId());
                ShowResult.add(showItem);
            }
        }
        return ShowResult;
    }

    public List<ShowOrderItem> getNotInvolved(List<ShowOrderItem> ShowResult){
        Iterator<ShowOrderItem> resultIter=ShowResult.iterator();
        while(resultIter.hasNext()){
            ShowOrderItem item=resultIter.next();
            if(item.getUserStatus()==0){
                resultIter.remove();
            }
        }
        return ShowResult;
    }

    public List<ShowOrderItem> getAllGeneralOrder(){
        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();

        List<ShowOrderItem> ShowResult=new ArrayList<>();
        Iterator<TradeOrderEntity> iterTradeOrder=tradeOrderList.iterator();
        while(iterTradeOrder.hasNext()){
            TradeOrderEntity TradeOrder= iterTradeOrder.next();
            ShowOrderItem showItem=new ShowOrderItem();
            showItem.setStatus(TradeOrder.getStatus());
            showItem.setTime(TradeOrder.getCreatetime());
            showItem.setTradeOrderId(TradeOrder.getTradeOrderId());
            ShowResult.add(showItem);
        }
        return ShowResult;
    }

    public List<ShowOrderItem> getDetailedOrder(TradeGameEntity tradeGame,List<ShowOrderItem> ShowResult,int i){
        ShowOrderGamesItem ShowGameItems = new ShowOrderGamesItem();
        ShowGameItems=getShowGameItem(tradeGame);

        List<ShowOrderGamesItem> temp = ShowResult.get(i).getGameDetail();
        temp.add(0, ShowGameItems);
        ShowResult.get(i).setGameDetail(temp);
        ShowResult.get(i).setUserStatus(1);

        return ShowResult;
    }

    public void confirmAsReceiver(TradeGameEntity tradeGame, ConfirmMatchJson address, int orderid){
        int tradeGameId=tradeGame.getTradeGameId();
        AddressEntity Address=addressRepo.findOne(address.getAddressId());
        tradeGameRepo.ConfirmByReceiver(tradeGameId,Address);
        //get the status in trade or minus by one shows one game order is confirmed
        if(tradeGame.getStatus()==1){
            tradeOrderRepo.confirmOneGame(orderid);
        }
    }

    public void confirmAsSender(TradeGameEntity tradeGame, ConfirmMatchJson address, int orderid){
        int tradeGameId=tradeGame.getTradeGameId();
        AddressEntity Address=addressRepo.findOne(address.getAddressId());
        tradeGameRepo.ConfirmBySender(tradeGameId,Address);
        if(tradeGame.getStatus()==1) {
            tradeOrderRepo.confirmOneGame(orderid);
        }
    }

    public void refuseAsReceiver(int tradeGameId){
        tradeGameRepo.RefuseByReceiver(tradeGameId);
    }

    public void refuseAsSender(int tradeGameId){
        tradeGameRepo.RefuseBySender(tradeGameId);
    }

    public void cancelOrder(int orderid){
        tradeOrderRepo.cancelOrder(orderid);
    }

    public TradeOrderEntity saveTradeOrder(TradeOrderEntity tradeOrder, Timestamp time,int gameNum,int orderid){
        tradeOrder.setCreatetime(time);
        tradeOrder.setStatus(gameNum);
        tradeOrder.setTradeOrderId(orderid);
        tradeOrderRepo.saveAndFlush(tradeOrder);
        return tradeOrder;
    }

    public int getNewOrderId(){
        int orderId=1;
        if(tradeOrderRepo.findAll()!=null){
            orderId=tradeOrderRepo.getMaxId()+1;
        }
        return orderId;
    }

    public TradeGameEntity setSenderTradeGame(AddressEntity address, UserEntity user, GameEntity sendGame,UserEntity targetUser,int orderId){
        TradeGameEntity tradeGameOne=new TradeGameEntity();
        tradeGameOne.setFromAddress(address);
        tradeGameOne.setSender(user);
        tradeGameOne.setGame(sendGame);
        tradeGameOne.setReceiver(targetUser);
        tradeGameOne.setSenderStatus(0);
        tradeGameOne.setReceiverStatus(1);
        tradeGameOne.setStatus(1);
        tradeGameOne.setTradeOrder(tradeOrderRepo.findOne(orderId));
        tradeGameRepo.saveAndFlush(tradeGameOne);

        return tradeGameOne;
    }

    public TradeGameEntity setReceiverTradeGame(AddressEntity address,UserEntity user,GameEntity receiveGame,UserEntity targetUser,int orderId){
        TradeGameEntity tradeGameTwo=new TradeGameEntity();
        tradeGameTwo.setTradeOrder(tradeOrderRepo.findOne(orderId));
        tradeGameTwo.setStatus(1);
        tradeGameTwo.setReceiverStatus(0);
        tradeGameTwo.setSenderStatus(1);
        tradeGameTwo.setReceiver(user);
        tradeGameTwo.setSender(targetUser);
        tradeGameTwo.setGame(receiveGame);
        tradeGameTwo.setToAddress(address);
        tradeGameRepo.saveAndFlush(tradeGameTwo);

        return tradeGameTwo;
    }

}
