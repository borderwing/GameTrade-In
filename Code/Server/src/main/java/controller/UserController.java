package controller;

import model.*;
import model.json.*;
import model.temporaryItem.ReceiverOrderItem;
import model.temporaryItem.SenderOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.*;
import sun.misc.Request;

import javax.validation.constraints.Null;
import javax.ws.rs.Path;
import javax.xml.ws.RequestWrapper;
import java.util.*;
import java.sql.Timestamp;

/**
 * Created by lykav on 2017/6/29.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private String apiPath = "/api/user";

    @Autowired
    UserRepository userRepo;
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    GameRepository gameRepo;
    @Autowired
    WishRepository wishRepo;
    @Autowired
    AddressRepository addressRepo;
    @Autowired
    PendingGameRepository pendingGameRepo;
    @Autowired
    OfferRepository offerRepo;
    @Autowired
    TradeOrderRepository tradeOrderRepo;
    @Autowired
    TradeGameRepository tradeGameRepo;

    // Retrieve Single User
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUser(@PathVariable("id") int id) {
        System.out.println("Fetching User with id " + id);
        UserEntity user = userRepo.findOne(id);
        if (user == null) {
            System.out.println("Cannot find User with id " + id);
            return new ResponseEntity<UserEntity>(HttpStatus.NOT_FOUND);
        }

        CustomerEntity customer = customerRepo.findOne(user.getUserId());
        if (customer != null) {
            return new ResponseEntity<UserEntity>(customer, HttpStatus.OK);
        }

        return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
    }


    // Fetch wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.GET)
    public ResponseEntity<List<WishEntity>> getWishList(
            @PathVariable("userId") int userId) {
        UserEntity user = userRepo.findByUserIdAndFetchWishlist(userId);
        if (user == null) {
            System.out.println("Cannot find User with id " + userId);
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<WishEntity> wishList = user.getWishes();

        //get the available game
        Iterator<WishEntity> iter=wishList.iterator();
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==0){
                iter.remove();
            }
        }
        return new ResponseEntity<List<WishEntity>>((List<WishEntity>) wishList, HttpStatus.OK);
    }



    // add items to wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.POST)
    public ResponseEntity<WishEntity> addItemsToWishList(
            @PathVariable("userId") int userId,
            @RequestBody WishJsonItem wishJsonItem) {
        if (wishJsonItem == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.OK);
        }
        UserEntity user = userRepo.findOne(userId);
        if (user == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game = gameRepo.findOne(wishJsonItem.getGameId());
        if (game == null) return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);

        Timestamp time=new Timestamp(System.currentTimeMillis());

        List<WishEntity> wishList=wishRepo.findByUserAndGame(user,game);

        //check whether the game is available in wish list
        boolean isAvailable=false;
        Iterator<WishEntity> iter=wishList.iterator();
        while(iter.hasNext()){
            WishEntity wishItem=iter.next();
            if(wishItem.getStatus()==1){
                isAvailable=true;
                break;
            }
        }

        //the game is not in the list
        if(!isAvailable){
            WishEntity wish=new WishEntity();
            wish.setPoints(wishJsonItem.getPoints());
            wish.setStatus(1);
            wish.getWishEntityPK().setUser(user);
            wish.getWishEntityPK().setGame(game);
            wish.getWishEntityPK().setCreateTime(time);
            wishRepo.saveAndFlush(wish);
            return new ResponseEntity<WishEntity>(wish,HttpStatus.OK);
        }

        //game is in the list
        else{
            System.out.println("user " + user.getUserId() + " has already added game " +
                    game.getGameId() + " in her wish list.");
            return new ResponseEntity<WishEntity>(HttpStatus.CONFLICT);
        }
    }



    //remove item from wishlist
    @RequestMapping(value="{userid}/wishlist/{gameid}/delete",method=RequestMethod.PUT)
    public ResponseEntity<Void> deleteGameFromWishlist(
            @PathVariable("gameid")int gameid,
            @PathVariable("userid")int userid) {
        System.out.println("delete game...");

        //find the user
        UserEntity user = userRepo.findOne(userid);
        if (user == null) {
            System.out.println("cant find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //find the game
        GameEntity game = gameRepo.findOne(gameid);
        if (game == null) {
            System.out.println("cant find game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //set status and find whether it is available
        boolean isAvailable = false;
        List<WishEntity> wishlist = wishRepo.findByUserAndGame(user, game);
        Iterator<WishEntity> iter = wishlist.iterator();
        while (iter.hasNext()) {
            WishEntity wish = iter.next();
            if (wish.getStatus() == 1) {
                Timestamp createTime=wish.getWishEntityPK().getCreateTime();
                wishRepo.deleteWishGame(user,game,createTime);
                isAvailable = true;
            }
        }
        if (isAvailable) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }



    //modify game points in wish list
    @RequestMapping(value="{userid}/wishlist/{gameid}/modify",method=RequestMethod.PUT)
    public ResponseEntity<Void> resetGamePointFromWishList(
            @PathVariable("gameid")int gameid,
            @PathVariable("userid")int userid,
            @RequestBody ModifyWishJsonItem modifyItem){
        System.out.println("modifying the points...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("cant find the game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //reset status and find whether it is available
        boolean isAvailable=false;
        List<WishEntity> wishlist=wishRepo.findByUserAndGame(user,game);
        Iterator<WishEntity> iter=wishlist.iterator();
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==1){
                Timestamp time=wish.getWishEntityPK().getCreateTime();
                wishRepo.modifyWishGame(user,game,time,modifyItem.getPoints());
                isAvailable=true;
            }
        }

        if(isAvailable) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }



    //fetch all the offer
    @RequestMapping(value="/{userId}/offer",method=RequestMethod.GET)
    public ResponseEntity<List<OfferEntity>> getAllOffer(@PathVariable("userId")int userId){
        System.out.println("fetch all the offering games...");

        UserEntity user=userRepo.findByUserIdAndFetchOfferlist(userId);
        if(user==null){
            System.out.println("cant find the user");
            return new ResponseEntity<List<OfferEntity>>(HttpStatus.NOT_FOUND);
        }

        Collection<OfferEntity> offerlist=user.getOffers();
        //check the availability
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offerGame=iter.next();
            if(offerGame.getStatus()==0){
                iter.remove();
            }
        }

        return new ResponseEntity<List<OfferEntity>>((List<OfferEntity>)offerlist,HttpStatus.OK);
    }



    //add offer game
    @RequestMapping(value="/{userId}/offer",method=RequestMethod.POST)
    public ResponseEntity<OfferEntity> addItemsToOfferList(@PathVariable("userId")int userid,
        @RequestBody OfferJsonItem offerGame){
        System.out.println("add game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find the user...");
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(offerGame.getGameId());
        if(game==null){
            System.out.println("cant find the game...");
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        Timestamp time=new Timestamp(System.currentTimeMillis());

        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offer =iter.next();
            if(offer.getStatus()==1){
                isAvailable=true;
                break;
            }
        }

        if(!isAvailable){
            OfferEntity offer=new OfferEntity();
            offer.setPoints(offerGame.getPoints());
            offer.setStatus(1);
            offer.getOfferEntityPK().setUser(user);
            offer.getOfferEntityPK().setGame(game);
            offer.getOfferEntityPK().setCreateTime(time);
            offerRepo.saveAndFlush(offer);
            return new ResponseEntity<OfferEntity>(offer,HttpStatus.OK);
        }
        else{
            System.out.println("the game is already in the offer list...");
            return new ResponseEntity<OfferEntity>(HttpStatus.CONFLICT);
        }
    }



    //delete a game from offer list
    @RequestMapping(value="/{userId}/offer/{gameId}/delete",method=RequestMethod.PUT)
    public ResponseEntity<Void> deleteItemFromOfferList(@PathVariable("userId")int userid,
                                                               @PathVariable("gameId")int gameid){
        System.out.println("delete game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("cant find game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                Timestamp time=offer.getOfferEntityPK().getCreateTime();
                offerRepo.deleteOfferGame(game,user,time);
                isAvailable=true;
            }
        }
        if(isAvailable){
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }



    //modify a game from offer list
    @RequestMapping(value="/{userId}/offer/{gameId}/modify",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyItemFromOfferList(@PathVariable("userId")int userid,
                                                        @PathVariable("gameId")int gameid,
                                                        @RequestBody ModifyOfferJsonItem modifyPoints){
        System.out.println("modify the points...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("cant find the game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<OfferEntity> offerList = offerRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerList.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                Timestamp time=offer.getOfferEntityPK().getCreateTime();
                offerRepo.modifyOfferGame(game,user,time,modifyPoints.getPoints());
                isAvailable=true;
            }
        }
        if(isAvailable){
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }

    // Fetch address
    @RequestMapping(value = "/{userId}/address", method = RequestMethod.GET)
    public ResponseEntity<List<AddressEntity>> getAddresses(
            @PathVariable("userId") int userId) {
        UserEntity user = userRepo.findByUserIdAndFetchAddresses(userId);
        if (user == null) {
            System.out.println("Cannot find User with id " + userId);
            return new ResponseEntity<List<AddressEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<AddressEntity> addresses = user.getAddresses();
        return new ResponseEntity<List<AddressEntity>>((List<AddressEntity>) addresses, HttpStatus.OK);
    }



    //Create Game to pending game
    @RequestMapping(value = "/{userId}/createGame", method = RequestMethod.POST)
    public ResponseEntity<Void> createGame(@RequestBody CreateGameJsonItem gameItem, @PathVariable("userId") int userId) {
        //check the game whether duplicated
        System.out.println("check the game title...");
        List<GameEntity> currentGame=gameRepo.findByTitle(gameItem.getTitle());
        System.out.println(currentGame.size());
        if(currentGame.size()!=0){
            System.out.println("the game is already in library");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //create game
        System.out.println("the game is not in the library and now creating the game");
        UserEntity user=userRepo.findOne(userId);
        if(user==null){
            System.out.println("user cant found");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        PendingGameEntity pendingGame=new PendingGameEntity();
        pendingGame.setLanguage(gameItem.getLanguage());
        pendingGame.setGenre(gameItem.getGenre());
        pendingGame.setPlatform(gameItem.getPlatform());
        pendingGame.setTitle(gameItem.getTitle());
        pendingGame.setStatus(0);
        pendingGame.setProposer(user);

        pendingGameRepo.saveAndFlush(pendingGame);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }



    //match game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}/match",method=RequestMethod.GET)
    public ResponseEntity<List<ReceiverOrderItem>> matchWistList(@PathVariable("userId")int userid,
                                                            @PathVariable("gameId")int gameid){
        System.out.println("match game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find user...");
            return new ResponseEntity<List<ReceiverOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("cant find game...");
            return new ResponseEntity<List<ReceiverOrderItem>>(HttpStatus.NOT_FOUND);
        }


        //get the wanted points
        int wantPoint=0;
        List<WishEntity> wishList=wishRepo.findByUserAndGame(user,game);
        Iterator<WishEntity> iter=wishList.iterator();
        System.out.println("2");
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==1){
                wantPoint=wish.getPoints();
                break;
            }
        }
        System.out.println(wantPoint);
        //get the list of userid
        List<OfferEntity> offerList=offerRepo.getOfferGame(wantPoint,gameid);
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("the number of the user owning the game is "+offerList.size());
        System.out.println("-----------------------------------------------------------------------");

        Iterator<OfferEntity> iterOfferList=offerList.iterator();
        List<Integer> offerUserid=new ArrayList<>();
        while(iterOfferList.hasNext()){
            OfferEntity offerentity=iterOfferList.next();
            offerUserid.add(offerentity.getOfferEntityPK().getUser().getUserId());
        }

        System.out.println("the size of the list is "+offerUserid.size());
        //匹配成立情况  用户的offer 和能提供用户的wishlist 笛卡尔集
        List<Integer> sendingGame;
        List<ReceiverOrderItem> resultOrder=new ArrayList<>();
        for(int i =0;i<offerUserid.size();i++){
            sendingGame=wishRepo.getSameGame(offerUserid.get(i),userid,wantPoint);
            for(int j =0;j<sendingGame.size();j++){
                ReceiverOrderItem orderItem=new ReceiverOrderItem();
                orderItem.setGetGameId(sendingGame.get(j));
                orderItem.setOfferGameId(gameid);
                orderItem.setSenderId(offerUserid.get(i));
                resultOrder.add(orderItem);
            }
        }
        return new ResponseEntity<List<ReceiverOrderItem>>(resultOrder,HttpStatus.OK);
    }


    //confirm the match
    @RequestMapping(value="/{userid}/wishlist/{gameId}/match/confirm",method=RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmMatch(@RequestBody CreateOrderJsonItem orderItem,
                                                         @PathVariable("gameId")int gameid,
                                                         @PathVariable("userid")int userid){

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("cant find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        UserEntity targetUser=userRepo.findOne(orderItem.getTargetUserId());
        if(targetUser==null){
            System.out.println("cant find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity sendGame=gameRepo.findOne(orderItem.getSenderGameId());
        if(sendGame==null){
            System.out.println("cant find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity receiveGame=gameRepo.findOne(gameid);
        if(receiveGame==null){
            System.out.println("cant find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        tradeOrder.setCreatetime(time);
        tradeOrder.setStatus(2);
        int orderId=tradeOrderRepo.getMaxId()+1;
        tradeOrder.setTradeOrderId(orderId);
        tradeOrderRepo.saveAndFlush(tradeOrder);

        //create TradeGame
        AddressEntity address=addressRepo.findOne(orderItem.getAddressId());

        //create the send game order
        int tradeGameId=tradeGameRepo.getMaxId()+1;

        TradeGameEntity tradeGameOne=new TradeGameEntity();
        tradeGameOne.setTradeOrder(tradeOrder);
        tradeGameOne.setTradeGameId(tradeGameId);
        tradeGameOne.setFromAddress(address);
        tradeGameOne.setSender(user);
        tradeGameOne.setGame(sendGame);
        tradeGameOne.setReceiver(targetUser);
        tradeGameOne.setSenderStatus(0);
        tradeGameOne.setReceiverStatus(1);
        tradeGameOne.setStatus(1);
        tradeGameOne.setTradeOrder(tradeOrderRepo.findOne(orderId));
        tradeGameRepo.saveAndFlush(tradeGameOne);

        //create the receive game order
        TradeGameEntity tradeGameTwo=new TradeGameEntity();
        tradeGameTwo.setTradeOrder(tradeOrder);
        tradeGameTwo.setTradeGameId(tradeGameId+1);
        tradeGameTwo.setTradeOrder(tradeOrderRepo.findOne(orderId));
        tradeGameTwo.setStatus(1);
        tradeGameTwo.setReceiverStatus(0);
        tradeGameTwo.setSenderStatus(1);
        tradeGameTwo.setReceiver(user);
        tradeGameTwo.setSender(targetUser);
        tradeGameTwo.setGame(receiveGame);
        tradeGameTwo.setToAddress(address);
        tradeGameRepo.saveAndFlush(tradeGameTwo);

        //add trade Game to TradeOrder
        List<TradeGameEntity> trade=new ArrayList<>();
        trade.add(tradeGameOne);
        trade.add(tradeGameTwo);
        tradeOrder.setTradeGames(trade);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }





    //match game in offer list
    /*@RequestMapping(value="{userId}/offer/{gameId}/match",method= RequestMethod.GET)
    public ResponseEntity<List<SenderOrderItem>> matchOfferList(@PathVariable("userId")int userid,
                                                                @PathVariable("gameId")int gameid){
        System.out.println("match game...");

        UserEntity user=userRepo.getOne(userid);
        if(user==null){
            System.out.println("cant find user...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.getOne(gameid);
        if(game==null){
            System.out.println("cant find game...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        int wantPoint=0;
        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                wantPoint=offer.getPoints();
                break;
            }
        }

        List<WishEntity> wishlist=wishRepo.getWishGame(wantPoint,gameid);

        List<Integer> wishUserId=new ArrayList<>();
        Iterator<WishEntity> iterWishList=wishlist.listIterator();
        while(iterWishList.hasNext()){
            WishEntity wishentitiy=iterWishList.next();
            wishUserId.add(wishentitiy.getWishEntityPK().getUser().getUserId());
        }

        List<SenderOrderItem> resultOrder=new ArrayList<>();
        List<Integer> offerGame;

        for(int i=0;i<wishUserId.size();i++){
            offerGame=offerRepo.getSameGame(userid,wishUserId.get(i),wantPoint);
            for(int j =0;j<offerGame.size();j++){
                SenderOrderItem orderItem=new SenderOrderItem();
                orderItem.setGetGameId(offerGame.get(j));
                orderItem.setOfferGameId(gameid);
                orderItem.setReceiverId(wishUserId.get(i));
                resultOrder.add(orderItem);
            }
        }

        return new ResponseEntity<List<SenderOrderItem>>(resultOrder,HttpStatus.OK);
    }*/
}