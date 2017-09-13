package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.assist.matchAssist;
import com.bankrupted.tradein.model.*;
import com.bankrupted.tradein.model.json.offer.CreateOfferJson;
import com.bankrupted.tradein.model.json.offer.ModifyOfferJson;
import com.bankrupted.tradein.model.json.order.BasicMatchConfirmJson;
import com.bankrupted.tradein.model.json.order.BasicMatchJson;
import com.bankrupted.tradein.model.json.order.ConfirmMatchJson;
import com.bankrupted.tradein.model.json.order.SeniorMatchConfirmJson;
import com.bankrupted.tradein.model.json.user.CreateAddressJson;
import com.bankrupted.tradein.model.json.user.RatingJson;
import com.bankrupted.tradein.model.json.wish.CreateWishJson;
import com.bankrupted.tradein.model.json.wish.ModifyWishJson;
import com.bankrupted.tradein.model.temporaryItem.*;
import com.bankrupted.tradein.service.*;
import com.sun.org.apache.regexp.internal.RE;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import com.bankrupted.tradein.repository.*;

import java.util.*;
import java.sql.Timestamp;

/**
 * Created by lykav on 2017/6/29.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    CustomerService customerService;
    @Autowired
    WishService wishService;
    @Autowired
    OfferService offerService;
    @Autowired
    AddressService addressService;
    @Autowired
    OrderService orderService;
    @Autowired
    GameService gameService;

    @Autowired
    GameRepository gameRepo;

    // Retrieve Single User
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUser(@PathVariable("id") int id) {
        System.out.println("Fetching User with id " + id);
        UserEntity user = userService.getUserById(id);
        if (user == null) {
            System.out.println("Cannot find User with id " + id);
            return new ResponseEntity<UserEntity>(HttpStatus.NOT_FOUND);
        }

        CustomerEntity customer = customerService.getCustomerById(user.getUserId());
        if (customer != null) {
            return new ResponseEntity<UserEntity>(customer, HttpStatus.OK);
        }

        return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
    }


    /*
                LIST CONTROLLER
     */

    @RequestMapping(value="/{userid}/wishlist",method = RequestMethod.GET)
    public ResponseEntity<List<WishEntity>> getWishList(@PathVariable("userid")int userid){
        UserEntity user=userService.getUserById(userid);
        if (user == null) {
            System.out.println("Cannot find User with id " + userid);
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<WishEntity> wishList=wishService.getAvailableWish(user);
        return new ResponseEntity<List<WishEntity>>((List<WishEntity>)wishList, HttpStatus.OK);
    }


    // Fetch wish list(paged)
    @RequestMapping(value = "/{userId}/wishlist/params", method = RequestMethod.GET)
    public ResponseEntity<List<WishEntity>> getWishListPaged(
            @PathVariable("userId") int userId,
            @RequestParam(value = "page",defaultValue = "0")Integer page,
            @RequestParam(value = "size",defaultValue = "5")Integer size) {
        UserEntity user=userService.getUserByIdAndFetchWishList(userId);
        if (user == null) {
            System.out.println("Cannot find User with id " + userId);
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<WishEntity> wishList=wishService.getAvailableWish(user);

        PagedListHolder<WishEntity> pagedWishList= new PagedListHolder<>((List<WishEntity>)wishList);
        pagedWishList.setPage(page);
        pagedWishList.setPageSize(size);
        return new ResponseEntity<List<WishEntity>>(pagedWishList.getPageList(), HttpStatus.OK);
    }


    //fetch single game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}",method=RequestMethod.GET)
    public ResponseEntity<WishEntity> getOneWish(@PathVariable("userId")int userid,
                                                 @PathVariable("gameId")long gameid){
        System.out.println("fetch single game...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }


        WishEntity wish=wishService.getOneWishByUserAndGame(user,game);
        if(wish==null){
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<WishEntity>(wish,HttpStatus.OK);
        }
    }


    // add items to wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.POST)
    public ResponseEntity<WishEntity> addItemsToWishList(
            @PathVariable("userId") int userId,
            @RequestBody CreateWishJson createWishJson) {

        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.getGameNonBlocked(createWishJson.getIgdbId(),createWishJson.getPlatformId(),createWishJson.getRegionId());
        if (game == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        Timestamp time=new Timestamp(System.currentTimeMillis());

        WishEntity wish=wishService.getOneWishByUserAndGame(user,game);

        //the game is not in the list
        if(wish==null){
            WishEntity NewWish=wishService.saveWishInAdd(createWishJson,user,game,time);
            return new ResponseEntity<WishEntity>(NewWish,HttpStatus.OK);
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
            @PathVariable("gameid")long gameid,
            @PathVariable("userid")int userid) {
        System.out.println("delete game...");

        //find the user
        UserEntity user = userService.getUserById(userid);
        if (user == null) {
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //find the game
        GameEntity game = gameService.fetchOneGame(gameid);
        if (game == null) {
            System.out.println("can't find game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        boolean isAvailable=wishService.removeWishItem(user,game);
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
            @PathVariable("gameid")long gameid,
            @PathVariable("userid")int userid,
            @RequestBody ModifyWishJson modifyItem){
        System.out.println("modifying the points...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            System.out.println("can't find the game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        boolean isAvailable=wishService.modifyWishItem(user,game,modifyItem);
        if(isAvailable) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }


    //basic match in wishlist
    @RequestMapping(value="{userid}/wishlist/match/params",method=RequestMethod.POST)
    public ResponseEntity<List<BasicMatchResultItem>> GetWishListManyToManyMatch(@RequestBody BasicMatchJson YouWantGames,
                                                                                 @PathVariable("userid")int userid,
                                                                                 @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                                                 @RequestParam(value = "size",defaultValue = "5")Integer size){
        System.out.println("match the games in wishlist");

        matchAssist assist=new matchAssist();

        int pointRange=YouWantGames.getPointRange();

        List<Long> YouWantGameList = assist.getGameIdList(YouWantGames.getYouWantGames());

        //get the points that you want to offer to get the wish games
        int Wantsum=0;
        for(int i =0;i<YouWantGameList.size();i++){
            Wantsum+=wishService.getWishPointsByIdAndGame(userid,YouWantGameList.get(i));
        }
        List<Integer> OfferRange=new ArrayList<>();
        OfferRange.add(Wantsum-pointRange);
        OfferRange.add(Wantsum+pointRange);

        //get the people who owns the game the customer want
        List<OfferEntity> OfferWantedGameList=offerService.findExceptById(userid);
        List<Integer> targetUserId=assist.getUsersOwnsWantingGames(OfferWantedGameList,YouWantGameList,OfferRange);


        List<OfferEntity> UserOffer=offerService.findByUserId(userid);
        Map<Long,Integer> UserOfferPoints=new HashMap<>();
        for(int i =0;i<UserOffer.size();i++){
            UserOfferPoints.put(UserOffer.get(i).getOfferEntityPK().getGame().getGameId(),UserOffer.get(i).getPoints());
            System.out.println(UserOffer.get(i).getOfferEntityPK().getGame().getGameId());
        }
        List<BasicMatchResultItem> result=new ArrayList<>();
        for(int i =0 ;i<targetUserId.size();i++){
            int TargetUserId = targetUserId.get(i);
            Map<Long,Integer> TargetUserWishPoints=new HashMap<>();
            List<WishEntity> TargetUserWishList=wishService.findByUserId(TargetUserId);
            for(int j =0;j< TargetUserWishList.size();j++){
                TargetUserWishPoints.put(TargetUserWishList.get(j).getWishEntityPK().getGame().getGameId(), TargetUserWishList.get(j).getPoints());
            }
            List<String> PotentialOfferGames=assist.getOfferGames(UserOfferPoints,TargetUserWishPoints,OfferRange.get(0),OfferRange.get(1));


            //get the result to print
            Iterator<String> StringIter=PotentialOfferGames.iterator();
            while(StringIter.hasNext()){
                String offerGame=StringIter.next();
                BasicMatchResultItem resultItem = new BasicMatchResultItem();
                resultItem.setTargetUserId(TargetUserId);
                resultItem.setYouOfferGame(offerGame);
                resultItem.setYouWantGame(YouWantGames.getYouWantGames());
                result.add(resultItem);
            }
        }
        return new ResponseEntity<List<BasicMatchResultItem>>(result,HttpStatus.OK);
    }

/*
    //confirm basic match
    @RequestMapping(value="{userid}/wishlist/match/confirm",method = RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmMatch(@RequestBody BasicMatchConfirmJson matchInfo,
                                                         @PathVariable("userid")int userid){
        System.out.println("confirming the match...");

        matchAssist matchassist=new matchAssist();

        //get the list of the gameId offering and wanting
        List<Long> youOfferGameIdList=matchassist.getGameIdList(matchInfo.getYouOfferGames());
        List<Long> youWantGameIdList=matchassist.getGameIdList(matchInfo.getYouWantGames());

        UserEntity user=userService.getUserById(userid);

        UserEntity TargetUser=userService.getUserById(matchInfo.getTargetUserId());

        //create trade order
        Timestamp time=new Timestamp(System.currentTimeMillis());
        int orderid=orderService.getNewOrderId();
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        tradeOrder=orderService.saveTradeOrder(tradeOrder,time,(youOfferGameIdList.size()+youWantGameIdList.size()),orderid);

        AddressEntity address=addressService.getAddressByUserAndId(user,matchInfo.getAddressid());

        List<TradeGameEntity> tradeGameList=new ArrayList<>();

        //set the TradeGames as sender
        for(int i = 0;i<youOfferGameIdList.size();i++){
            GameEntity game=gameService.fetchOneGame(youOfferGameIdList.get(i));
            WishEntity wish=wishService.getOneWishByUserAndGame(TargetUser,game);
            TradeGameEntity tradeGame=orderService.setSenderTradeGame(address,user,game,TargetUser,orderid,wish.getPoints());
            tradeGameList.add(tradeGame);
        }

        //set the tradeGames as receiver
        for(int i = 0;i<youWantGameIdList.size();i++){
            GameEntity game=gameService.fetchOneGame(youWantGameIdList.get(i));
            WishEntity wish=wishService.getOneWishByUserAndGame(user,game);
            TradeGameEntity tradeGame=orderService.setReceiverTradeGame(address,user,game,TargetUser,orderid,);
            tradeGameList.add(tradeGame);
        }
        tradeOrder.setTradeGames(tradeGameList);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }*/
/*

    //senior match in wishlist
    @RequestMapping(value="{userid}/wishlist/match/senior",method=RequestMethod.POST)
    public ResponseEntity<List<SeniorMatchResultItem>> getTripleMatch(@PathVariable("userid")int userid,
                                                          @RequestBody BasicMatchJson YouWantGames){
        System.out.println("get senior matching...");

        //check the user
        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<SeniorMatchResultItem>>(HttpStatus.NOT_FOUND);
        }

        matchAssist assist=new matchAssist();

        List<Long> YouWantGameList=assist.getGameIdList(YouWantGames.getYouWantGames());
        int pointRange=YouWantGames.getPointRange();

        //get the point range
        int WantPoints=0;
        for(int i =0;i<YouWantGameList.size();i++){
            WantPoints+=wishService.getWishPointsByIdAndGame(userid,YouWantGameList.get(i));
        }
        List<Integer> OfferRange=new ArrayList<>();
        OfferRange.add(WantPoints-pointRange);
        OfferRange.add(WantPoints+pointRange);

        List<SeniorMatchResultItem> seniorMatchResult=new ArrayList<>();


        //get the people who owns the people user want
        List<OfferEntity> OfferWantedGameList=offerService.findExceptById(userid);
        //targetUser as UserA
        List<Integer> targetUserId=assist.getUsersOwnsWantingGames(OfferWantedGameList,YouWantGameList,OfferRange);

        List<OfferEntity> UserOffer=offerService.findByUserId(userid);
        Map<Long,Integer> UserOfferPoints=new HashMap<>();
        for(int i =0;i<UserOffer.size();i++){
            UserOfferPoints.put(UserOffer.get(i).getOfferEntityPK().getGame().getGameId(),UserOffer.get(i).getPoints());
        }

        //AllUser As UserB
        List<Integer> AllUser=userService.getAllUserId();

        for(int i = 0; i < targetUserId.size(); i++){
            for(int j = 0; j < AllUser.size(); j++){
                if(AllUser.get(j) != targetUserId.get(i)){
                    Map<Long,Integer> UserAWishPoints=wishService.getGamePointsById(targetUserId.get(i));
                    Map<Long,Integer> UserBWishPoints=wishService.getGamePointsById(AllUser.get(j));
                    Map<Long,Integer> UserBOfferPoints=offerService.getGamePointsById(AllUser.get(j));
                    List<String> ResultUserAAndUserB=assist.getOfferGames(UserBOfferPoints,UserAWishPoints,OfferRange.get(0),OfferRange.get(1));
                    List<String> ResultUserBAndYou=assist.getOfferGames(UserOfferPoints,UserBWishPoints,OfferRange.get(0),OfferRange.get(1));
                    for(int AToBSize=0;AToBSize<ResultUserAAndUserB.size();AToBSize++) {
                        for (int YouToBSize = 0; YouToBSize < ResultUserBAndYou.size();YouToBSize++) {
                            SeniorMatchResultItem ResultItem = new SeniorMatchResultItem();
                            ResultItem.setUserAId(targetUserId.get(i));
                            ResultItem.setUserBId(AllUser.get(j));
                            ResultItem.setUserAOffer(YouWantGames.getYouWantGames());
                            ResultItem.setUserBOffer(ResultUserAAndUserB.get(AToBSize));
                            ResultItem.setYouOffer(ResultUserBAndYou.get(YouToBSize));
                            seniorMatchResult.add(ResultItem);
                        }
                    }
                }
            }
        }
        return new ResponseEntity<List<SeniorMatchResultItem>>(seniorMatchResult,HttpStatus.OK);
    }
*/
    //confirm senior match
/*    @RequestMapping(value="{userid}/wishlist/match/senior/confirm",method = RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmSeniorMatch(@PathVariable("userid")int userid,
                                                               @RequestBody SeniorMatchConfirmJson matchInfo){
        System.out.println("confirm the match");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        UserEntity userA=userService.getUserById(matchInfo.getUserAId());
        if(userA==null){
            System.out.println("can't find userA...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        UserEntity userB=userService.getUserById(matchInfo.getUserBId());
        if(userB==null){
            System.out.println("can't find userB...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        matchAssist assist=new matchAssist();

        List<Long> YouOfferList=assist.getGameIdList(matchInfo.getYouOffer());
        List<Long> UserAOfferList=assist.getGameIdList(matchInfo.getUserAOffer());
        List<Long> UserBOfferList=assist.getGameIdList(matchInfo.getUserBOffer());

        //create tradeOrder
        Timestamp time=new Timestamp(System.currentTimeMillis());
        int orderid=orderService.getNewOrderId();
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        tradeOrder=orderService.saveTradeOrder(tradeOrder,time,(YouOfferList.size()+ UserAOfferList.size()+UserBOfferList.size()),orderid);

        AddressEntity address=addressService.getAddressById(matchInfo.getAddresssId());

        List<TradeGameEntity> tradeGameList=new ArrayList<>();

        //set the tradeGames as sender
        for(int i = 0;i<YouOfferList.size();i++){
            GameEntity game=gameService.fetchOneGame(YouOfferList.get(i));
            TradeGameEntity tradeGame=orderService.setSenderTradeGame(address,user,game,userB,orderid);
            tradeGameList.add(tradeGame);
        }

        //set the tradeGames as receiver
        for(int i = 0;i<UserAOfferList.size();i++){
            GameEntity game=gameService.fetchOneGame(UserAOfferList.get(i));
            TradeGameEntity tradeGame=orderService.setReceiverTradeGame(address,user,game,userA,orderid);
            tradeGameList.add(tradeGame);
        }

        //set other tradeGames
        for(int i =0;i<UserBOfferList.size();i++){
            GameEntity game=gameService.fetchOneGame(UserBOfferList.get(i));
            TradeGameEntity tradeGame=orderService.setUnconfirmTradeGame(game,userB,userA,orderid);
            tradeGameList.add(tradeGame);
        }

        tradeOrder.setTradeGames(tradeGameList);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }*/
    /*
                    OFFER CONTROLLER
    */



    //fetch all the offer(paged)
    @RequestMapping(value="/{userId}/offerlist/params",method=RequestMethod.GET)
    public ResponseEntity<List<OfferEntity>> getAllOfferPaged(@PathVariable("userId")int userId,
                                                              @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                              @RequestParam(value = "size",defaultValue = "5")Integer size){
        System.out.println("fetch all the offering games...");

        UserEntity user=userService.getUserByIdAndFetchOfferList(userId);
        if(user==null){
            System.out.println("can't find the user");
            return new ResponseEntity<List<OfferEntity>>(HttpStatus.NOT_FOUND);
        }

        Collection<OfferEntity> offerlist=offerService.getAvailableOffer(user);

        //get paged
        PagedListHolder<OfferEntity> PagedOfferList=new PagedListHolder<>((List<OfferEntity>)offerlist);
        PagedOfferList.setPageSize(size);
        PagedOfferList.setPage(page);
        return new ResponseEntity<List<OfferEntity>>(PagedOfferList.getPageList(),HttpStatus.OK);
    }

    //fetch single offer
    @RequestMapping(value="/{userId}/offerlist/{gameId}",method=RequestMethod.GET)
    public ResponseEntity<OfferEntity> getOneOffer(@PathVariable("userId")int userid,
                                                   @PathVariable("gameId")long gameid){
        System.out.println("fetch single game...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }
        OfferEntity offer=offerService.getOneOfferByUserAndGame(user,game);
        if(offer==null){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<OfferEntity>(offer,HttpStatus.OK);
        }
    }


    //add offer game
    @RequestMapping(value="/{userId}/offerlist",method=RequestMethod.POST)
    public ResponseEntity<OfferEntity> addItemsToOfferList(@PathVariable("userId")int userid,
        @RequestBody CreateOfferJson offerGame){
        System.out.println("add game...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.getGameNonBlocked(offerGame.getIgdbId(),offerGame.getPlatformId(),offerGame.getRegionId());
        if (game == null) {
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        Timestamp time=new Timestamp(System.currentTimeMillis());

        OfferEntity offer=offerService.getOneOfferByUserAndGame(user,game);

        if(offer==null){
            OfferEntity newOffer=offerService.saveOfferInAdd(offerGame,user,game,time);
            return new ResponseEntity<OfferEntity>(newOffer,HttpStatus.OK);
        }
        else{
            System.out.println("the game is already in the offer list...");
            return new ResponseEntity<OfferEntity>(HttpStatus.CONFLICT);
        }
    }



    //delete a game from offer list
    @RequestMapping(value="/{userId}/offerlist/{gameId}/delete",method=RequestMethod.PUT)
    public ResponseEntity<Void> deleteItemFromOfferList(@PathVariable("userId")int userid,
                                                               @PathVariable("gameId")long gameid){
        System.out.println("delete game...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            System.out.println("can't find game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        boolean isAvailable=offerService.removeOfferItem(user,game);
        if(isAvailable){
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }



    //modify a game from offer list
    @RequestMapping(value="/{userId}/offerlist/{gameId}/modify",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyItemFromOfferList(@PathVariable("userId")int userid,
                                                        @PathVariable("gameId")long gameid,
                                                        @RequestBody ModifyOfferJson modifyPoints){
        System.out.println("modify the points...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            System.out.println("can't find the game...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        boolean isAvailable=offerService.modifyOfferItem(user,game,modifyPoints);
        if(isAvailable){
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
    }


    /*
            ADDRESS CONTROLLER
     */




    // Fetch address
    @RequestMapping(value = "/{userId}/address", method = RequestMethod.GET)
    public ResponseEntity<List<AddressEntity>> getAddresses(
            @PathVariable("userId") int userId) {
        UserEntity user = userService.getUserByIdAndFetchAddress(userId);
        if (user == null) {
            System.out.println("Cannot find User with id " + userId);
            return new ResponseEntity<List<AddressEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<AddressEntity> addresses = user.getAddresses();
        return new ResponseEntity<List<AddressEntity>>((List<AddressEntity>) addresses, HttpStatus.OK);
    }

    // fetch address(paged)
    @RequestMapping(value="/{userId}/address/params",method=RequestMethod.GET)
    public ResponseEntity<List<AddressEntity>> getAddressPaged(@PathVariable("userId")int userId,
                                                               @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",defaultValue = "5")Integer size){
        UserEntity user = userService.getUserByIdAndFetchAddress(userId);
        if(user==null){
            System.out.println("cannot find the user...");
            return new ResponseEntity<List<AddressEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<AddressEntity> addresses=user.getAddresses();

        //get paged
        PagedListHolder<AddressEntity> pagedAddress=new PagedListHolder<>((List<AddressEntity>)addresses);
        pagedAddress.setPage(page);
        pagedAddress.setPageSize(size);
        return new ResponseEntity<List<AddressEntity>>(pagedAddress.getPageList(),HttpStatus.OK);
    }

    //fetch one address
    @RequestMapping(value="/{userId}/address/{addressid}",method=RequestMethod.GET)
    public ResponseEntity<AddressEntity> getOneAddress(@PathVariable("userId")int userid,
                                                       @PathVariable("addressid")int addressid){
        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user");
            return new ResponseEntity<AddressEntity>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=addressService.getAddressByUserAndId(user,addressid);

        if(address==null){
            System.out.println("can't find the address");
            return new ResponseEntity<AddressEntity>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AddressEntity>(address,HttpStatus.OK);
    }

    //Add more address
    @RequestMapping(value="/{userid}/address",method=RequestMethod.POST)
    public ResponseEntity<AddressEntity> addAddress(@RequestBody CreateAddressJson addressItem,
                                                    @PathVariable("userid")int userid){
        System.out.println("creating new address");

        //check whether all blanks are filled
        if(addressItem.getAddress()==""||addressItem.getPhone()==""||addressItem.getReceiver()==""||addressItem.getRegion()==""){
            System.out.println("dont't fill all the blanks...");
            return new ResponseEntity<AddressEntity>(HttpStatus.BAD_REQUEST);
        }

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user");
            return new ResponseEntity<AddressEntity>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=addressService.addNewAddress(addressItem,user);

        return new ResponseEntity<AddressEntity>(address,HttpStatus.OK);
    }



    //modify address
    @RequestMapping(value="/{userid}/address/{addressid}",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyAddress(@RequestBody CreateAddressJson addressItem,
                                                       @PathVariable("userid")int userid,
                                                       @PathVariable("addressid")int addressid){
        System.out.println("modify the address...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=addressService.getAddressByUserAndId(user,addressid);
        if(address==null){
            System.out.println("can't find address");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        addressService.updateAddress(addressItem,addressid);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    /*
            MATCH CONTROLLER
     */


    //match game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}/match",method=RequestMethod.GET)
    public ResponseEntity<List<ReceiverOrderItem>> matchWistList(
            @RequestParam(name = "scale",defaultValue = "200")Integer scale,@PathVariable("userId")int userid,
                                                            @PathVariable("gameId")long gameid){
        System.out.println("match game...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<ReceiverOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            System.out.println("can't find game...");
            return new ResponseEntity<List<ReceiverOrderItem>>(HttpStatus.NOT_FOUND);
        }

        //get the wanted points
        int wantPoint=0;
        List<WishEntity> wishList=wishService.findByUserAndGame(user,game);
        Iterator<WishEntity> iter=wishList.iterator();
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==1){
                wantPoint=wish.getPoints();
                break;
            }
        }
        //get the list of userid
        List<OfferEntity> offerList=offerService.getOfferGames(wantPoint,gameid,scale);

        Iterator<OfferEntity> iterOfferList=offerList.iterator();
        List<Integer> offerUserid=new ArrayList<>();
        while(iterOfferList.hasNext()){
            OfferEntity offerentity=iterOfferList.next();
            int UserId=offerentity.getOfferEntityPK().getUser().getUserId();
            if(UserId!=userid){
                offerUserid.add(UserId);
            }
        }
        List<Long> sendingGame;
        List<ReceiverOrderItem> resultOrder=new ArrayList<>();
        for(int i =0;i<offerUserid.size();i++){
            sendingGame=wishService.getSameGame(offerUserid.get(i),userid,wantPoint,scale);
            for(int j =0;j<sendingGame.size();j++){
                ReceiverOrderItem orderItem=new ReceiverOrderItem();
                GameEntity OfferGame=gameService.fetchOneGame(sendingGame.get(j));
                orderItem.setOfferGame(OfferGame);
                orderItem.setWishGame(game);
                UserEntity sender=userService.getUserById(offerUserid.get(i));
                orderItem.setSender(sender);
                WishEntity wish=wishService.getOneWishByUserAndGame(userService.getUserById(offerUserid.get(i)),OfferGame);
                orderItem.setOfferPoint(wish.getPoints());
                resultOrder.add(orderItem);
            }
        }
        return new ResponseEntity<List<ReceiverOrderItem>>(resultOrder,HttpStatus.OK);
    }


    //confirm the match
    @RequestMapping(value="/{userid}/wishlist/{gameId}/match/confirm",method=RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmWishMatch(@RequestBody ConfirmMatchJson orderItem,
                                                         @PathVariable("gameId")long gameid,
                                                         @PathVariable("userid")int userid){

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        UserEntity targetUser=userService.getUserById(orderItem.getTargetUserId());
        if(targetUser==null){
            System.out.println("can't find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity sendGame=gameService.fetchOneGame(orderItem.getGameId());
        if(sendGame==null){
            System.out.println("can't find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity receiveGame=gameService.fetchOneGame(gameid);
        if(receiveGame==null){
            System.out.println("can't find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        //check whether the order is already exist
        if(orderService.DuplicatedOrder(gameid,userid)){
            System.out.println("have order already...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.CONFLICT);
        }


        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        int orderId=orderService.getNewOrderId();
        orderService.saveTradeOrder(tradeOrder,time,2,orderId);

        //create TradeGame
        AddressEntity address=addressService.getAddressById(orderItem.getAddressId());

        //get the points
        WishEntity UserWish=wishService.getOneWishByUserAndGame(user,receiveGame);
        WishEntity TargetWish=wishService.getOneWishByUserAndGame(targetUser,sendGame);
        OfferEntity UserOffer=offerService.getOneOfferByUserAndGame(user,sendGame);
        OfferEntity TargetOffer=offerService.getOneOfferByUserAndGame(targetUser,receiveGame);
        int receivePoints=(UserWish.getPoints()+TargetOffer.getPoints())/2;
        int offerPoints=(UserOffer.getPoints()+TargetWish.getPoints())/2;

        //create the send game order
        TradeGameEntity tradeGameOne=orderService.setSenderTradeGame(address,user,sendGame,targetUser,orderId,offerPoints);

        //create the receive game order
        TradeGameEntity tradeGameTwo=orderService.setReceiverTradeGame(address,user,receiveGame,targetUser,orderId,receivePoints);

        //add trade Game to TradeOrder
        List<TradeGameEntity> trade=new ArrayList<>();
        trade.add(tradeGameOne);
        trade.add(tradeGameTwo);
        tradeOrder.setTradeGames(trade);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }


    //match game in offer list
    @RequestMapping(value="/{userid}/offerlist/{gameid}/match",method=RequestMethod.GET)
    public ResponseEntity<List<SenderOrderItem>> matchOfferList(
            @RequestParam(name = "scale",defaultValue = "200")Integer scale,
                                                                @PathVariable("userid")int userid,
                                                                @PathVariable("gameid")long gameid){
        System.out.println("match the game in offer list...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameService.fetchOneGame(gameid);
        if(game==null){
            System.out.println("can't find game...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        //get the wanted points
        int wantPoint=0;
        List<OfferEntity> offerList=offerService.findByUserAndGame(user,game);
        Iterator<OfferEntity> offerIter=offerList.iterator();
        while(offerIter.hasNext()){
            OfferEntity offer=offerIter.next();
            if(offer.getStatus()==1){
                wantPoint=offer.getPoints();
                break;
            }
        }

        List<WishEntity> wishList=wishService.getWishGame(wantPoint,gameid);
        List<Integer> wishListUserId=new ArrayList<>();
        Iterator<WishEntity> wishListIter=wishList.iterator();

        while(wishListIter.hasNext()) {
            WishEntity wish = wishListIter.next();
            int UserId = wish.getWishEntityPK().getUser().getUserId();
            if (UserId != userid) {
                wishListUserId.add(UserId);
            }
        }

        List<Long> receivingGame;
        List<SenderOrderItem> resultOrder=new ArrayList<>();
        for(int i =0;i<wishListUserId.size();i++){
            receivingGame=offerService.getSameGame(userid,wishListUserId.get(i),wantPoint);
            for(int j =0;j<receivingGame.size();j++){
                SenderOrderItem senderItem=new SenderOrderItem();
                senderItem.setReceiverId(wishListUserId.get(i));
                senderItem.setOfferGameId(gameid);
                senderItem.setGetGameId(receivingGame.get(j));
                resultOrder.add(senderItem);
            }
        }

        return new ResponseEntity<List<SenderOrderItem>>(resultOrder,HttpStatus.OK);

    }


    //confirm the match in offer list
    @RequestMapping(value="/{userid}/offerlist/{gameid}/match/confirm",method=RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmOfferMatch(@RequestBody ConfirmMatchJson orderItem,
                                                              @PathVariable("gameid")long gameid,
                                                              @PathVariable("userid")int userid){
        UserEntity user=userService.getUserById(userid);

        UserEntity targetUser=userService.getUserById(orderItem.getTargetUserId());

        GameEntity SendGame=gameService.fetchOneGame(gameid);

        GameEntity ReceiveGame=gameService.fetchOneGame(orderItem.getGameId());


        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        int orderId=orderService.getNewOrderId();
        orderService.saveTradeOrder(tradeOrder,time,2,orderId);

        //create trade game entity
        AddressEntity address=addressService.getAddressById(orderItem.getAddressId());

        //create send game order
        TradeGameEntity tradeGameOne=orderService.setSenderTradeGame(address,user,SendGame,targetUser,orderId,wishService.getOneWishByUserAndGame(targetUser,SendGame).getPoints());

        //create the receive game order
        TradeGameEntity tradeGameTwo=orderService.setReceiverTradeGame(address,user,ReceiveGame,targetUser,orderId,wishService.getOneWishByUserAndGame(user, ReceiveGame).getPoints());

        //add trade Game to TradeOrder
        List<TradeGameEntity> trade=new ArrayList<>();
        trade.add(tradeGameOne);
        trade.add(tradeGameTwo);
        tradeOrder.setTradeGames(trade);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }

    /*
            ORDER CONTROLLER
     */


    //fetch all orders (paged)
    @RequestMapping(value="/{userid}/order/params",method=RequestMethod.GET)
    public ResponseEntity<List<OrderResult>> getAllOrdersPaged(@PathVariable("userid")int userid,
                                                                 @RequestParam(value = "size",defaultValue = "5")Integer size,
                                                                 @RequestParam(value = "page",defaultValue = "0")Integer page){
        System.out.println("fetch all orders");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<OrderResult>>(HttpStatus.NOT_FOUND);
        }
        List<ShowOrderItem> ShowResult=orderService.getAllGeneralOrder();

        List<TradeGameEntity> tradeGameList=orderService.getAllTradeGames();
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            int orderid=tradeGame.getTradeOrder().getTradeOrderId();
            int ReceiverId=tradeGame.getReceiver().getUserId();
            int SenderId=tradeGame.getSender().getUserId();
            for(int i =0;i<ShowResult.size();i++){
                if(orderid==ShowResult.get(i).getTradeOrderId()&&(ReceiverId==userid||SenderId==userid)){
                    ShowResult=orderService.getDetailedOrder(tradeGame,ShowResult,i);
                }
            }
        }
        Iterator<ShowOrderItem> resultIter=ShowResult.iterator();
        while(resultIter.hasNext()){
            ShowOrderItem item=resultIter.next();
            if(item.getUserStatus()==0){
                resultIter.remove();
            }
        }

        List<OrderResult> resultList=new ArrayList<>();
        Iterator<ShowOrderItem> iterResult=ShowResult.iterator();
        while(iterResult.hasNext()){
            ShowOrderItem item=iterResult.next();
            if(item.getGameDetail().size()==2) {
                OrderResult showResult = new OrderResult();
                int targetUserId;
                if (item.getGameDetail().get(0).getReceiver().getUserId() == userid) {
                    GameEntity gameWish = gameRepo.findOne(item.getGameDetail().get(0).getGameId());
                    GameEntity gameOffer = gameRepo.findOne(item.getGameDetail().get(1).getGameId());
                    showResult.setWishGame(gameWish);
                    showResult.setOfferGame(gameOffer);
                    showResult.setWishPoints(item.getGameDetail().get(0).getPoints());
                    showResult.setOfferPoints(item.getGameDetail().get(1).getPoints());
                    showResult.setYouAddress(item.getGameDetail().get(0).getToAddress());
                    showResult.setTargetAddress(item.getGameDetail().get(0).getFromAddress());
                } else {
                    GameEntity gameWish = gameRepo.findOne(item.getGameDetail().get(1).getGameId());
                    showResult.setWishGame(gameWish);
                    GameEntity gameOffer = gameRepo.findOne(item.getGameDetail().get(0).getGameId());
                    showResult.setOfferGame(gameOffer);
                    showResult.setWishPoints(item.getGameDetail().get(1).getPoints());
                    showResult.setOfferPoints(item.getGameDetail().get(0).getPoints());
                    showResult.setYouAddress(item.getGameDetail().get(1).getToAddress());
                    showResult.setTargetAddress(item.getGameDetail().get(1).getFromAddress());
                }
                showResult.setOrderId(item.getTradeOrderId());
                showResult.setStatus(item.getStatus());
                resultList.add(showResult);
            }
        }

        PagedListHolder<OrderResult> pagedShowResult=new PagedListHolder<>(resultList);
        pagedShowResult.setPageSize(size);
        pagedShowResult.setPage(page);
        return new ResponseEntity<List<OrderResult>>(pagedShowResult.getPageList(),HttpStatus.OK);
    }

    //fetch one order
    @RequestMapping(value="/{userid}/order/{orderId}",method=RequestMethod.GET)
    public ResponseEntity<OrderResult> getOneOrder(@PathVariable("userid")int userid,@PathVariable("orderId")int orderId){
        System.out.println("get one order...");
        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<OrderResult>(HttpStatus.NOT_FOUND);
        }

        TradeOrderEntity order=orderService.getTradeOrderById(orderId);
        List<TradeGameEntity> TradeGames=orderService.getTradeGamesById(orderId);
        OrderResult result=new OrderResult();
        if(TradeGames.get(0).getReceiver().getUserId()==userid){
            result.setTargetAddress(TradeGames.get(0).getFromAddress());
            result.setYouAddress(TradeGames.get(0).getToAddress());
            result.setStatus(order.getStatus());
            result.setOrderId(orderId);
            result.setOfferGame(TradeGames.get(1).getGame());
            result.setWishGame(TradeGames.get(0).getGame());
            result.setOfferPoints(TradeGames.get(1).getPoints());
            result.setWishPoints(TradeGames.get(0).getPoints());
        }
        else{
            result.setTargetAddress(TradeGames.get(1).getFromAddress());
            result.setYouAddress(TradeGames.get(1).getToAddress());
            result.setStatus(order.getStatus());
            result.setOrderId(orderId);
            result.setOfferGame(TradeGames.get(0).getGame());
            result.setWishGame(TradeGames.get(1).getGame());
            result.setWishPoints(TradeGames.get(1).getPoints());
            result.setOfferPoints(TradeGames.get(0).getPoints());
        }
        return new ResponseEntity<OrderResult>(result,HttpStatus.OK);
    }

    //fetch unconfirmed order
    @RequestMapping(value="/{userid}/order/unconfirmed",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getUnconfirmOrders(@PathVariable("userid")int userid){
        System.out.println("fetch all orders");
        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }
        List<ShowOrderItem> ShowResult=orderService.getUnconfirmedGeneralOrder();

        List<TradeGameEntity> tradeGameList=orderService.getAllTradeGames();
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            if(tradeGame.getStatus()>0) {
                int orderid = tradeGame.getTradeOrder().getTradeOrderId();
                int ReceiverId = tradeGame.getReceiver().getUserId();
                int ReceiverStatus = tradeGame.getReceiverStatus();
                int SenderId = tradeGame.getSender().getUserId();
                int SenderStatus = tradeGame.getSenderStatus();
                for (int i = 0; i < ShowResult.size(); i++) {
                    //check whether the order is unconfirmed
                    if (ShowResult.get(i).getStatus() > 0 && orderid == ShowResult.get(i).getTradeOrderId() && ((ReceiverId == userid && ReceiverStatus == 1) || (SenderId == userid && SenderStatus == 1))) {
                        //crete the order to be showed
                        ShowResult=orderService.getDetailedOrder(tradeGame,ShowResult,i);
                    }
                }
            }
        }

        //remove those orders without the involve of user
        ShowResult=orderService.getNotInvolved(ShowResult);

        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);
    }


    //get finished order
    @RequestMapping(value="/{userid}/order/finished",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getFinishedOrders(@PathVariable("userid")int userid){
        System.out.println("fetching the finished orders");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }
        List<ShowOrderItem> ShowResult=orderService.getFinishedGeneralOrder();

        List<TradeGameEntity> tradeGameList=orderService.getAllTradeGames();
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            if(tradeGame.getStatus()>0) {
                int orderid = tradeGame.getTradeOrder().getTradeOrderId();
                int ReceiverId = tradeGame.getReceiver().getUserId();
                int SenderId = tradeGame.getSender().getUserId();
                for (int i = 0; i < ShowResult.size(); i++) {
                    //check whether the order is unconfirmed
                    if (orderid == ShowResult.get(i).getTradeOrderId() && (ReceiverId == userid || SenderId == userid)) {
                        //crete the order to be showed
                        ShowResult=orderService.getDetailedOrder(tradeGame,ShowResult,i);
                    }
                }
            }
        }

        ShowResult=orderService.getNotInvolved(ShowResult);
        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);
    }

    //confirm the order
    @RequestMapping(value="/{userid}/order/{orderid}/confirm",method=RequestMethod.PUT)
    public ResponseEntity<Void> confirmOrder(@PathVariable("userid")int userid,
                                             @PathVariable("orderid")int orderid,
                                             @RequestBody com.bankrupted.tradein.model.json.offer.ConfirmMatchJson address){
        System.out.println("confirm the order...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        TradeOrderEntity tradeOrder=orderService.getTradeOrderById(orderid);
        if(tradeOrder==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<TradeGameEntity> tradeGameList=orderService.getTradeGamesById(orderid);
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            //confirm as receiver
            if(tradeGame.getReceiver().getUserId()==userid){
                orderService.confirmAsReceiver(tradeGame,address,orderid);
            }
            //confirm as sender
            else if(tradeGame.getSender().getUserId()==userid){
                orderService.confirmAsSender(tradeGame,address,orderid);;
            }
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    //refuse the order
    @RequestMapping(value="/{userid}/order/{orderid}/refuse",method=RequestMethod.PUT)
    public ResponseEntity<Void> refuseOrder(@PathVariable("userid")int userid,
                                             @PathVariable("orderid")int orderid){
        System.out.println("refusing order...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        TradeOrderEntity tradeOrder=orderService.getTradeOrderById(orderid);
        if(tradeOrder==null){
            System.out.println("can't find order...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<TradeGameEntity> tradeGameList=orderService.getTradeGamesById(orderid);
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            //check the game order as receiver
            if(tradeGame.getReceiver().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                orderService.refuseAsReceiver(tradeGameId);
            }
            //check game order as sender
            else if(tradeGame.getSender().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                orderService.refuseAsSender(tradeGameId);
            }
        }
        //update the status in trade order
        orderService.cancelOrder(orderid);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    //               User Rating


    //get user rating
    @RequestMapping(value="/{userid}/rating",method=RequestMethod.GET)
    public ResponseEntity<Integer> getRating(@PathVariable("userid")int userid){
        System.out.println("get the rating...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
        }

        int ratingUserNum=userService.getRaingUserNumById(userid);
        if(ratingUserNum==0){
            System.out.print("no one rating...");
            return new ResponseEntity<Integer>(HttpStatus.NO_CONTENT);
        }

        int rating=userService.getUserRatingById(userid);

        return new ResponseEntity<Integer>(rating,HttpStatus.OK);
    }

    //rating
    @RequestMapping(value="/{userid}/rating/{targetUserid}",method=RequestMethod.PUT)
    public ResponseEntity<Void> rating(@PathVariable("userid")int userid,
                                       @PathVariable("targetUserid")int targetUserid,
                                       @RequestBody RatingJson points){
        System.out.println("rating...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        UserEntity TargetUser=userService.getUserById(targetUserid);
        if(TargetUser==null){
            System.out.println("can't find targetUser...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        int ratingPoints=points.getRatingPoints();
        if(ratingPoints>100 || ratingPoints<0){
            System.out.println("invalid rating points...");
            return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED);
        }

        customerService.UpdateRating(targetUserid,ratingPoints);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}