package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.assist.matchAssist;
import com.bankrupted.tradein.assist.matchAssist.*;
import com.bankrupted.tradein.model.*;
import com.bankrupted.tradein.model.json.*;
import com.bankrupted.tradein.model.temporaryItem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankrupted.tradein.repository.*;

import java.util.*;
import java.sql.Timestamp;

import static org.python.icu.text.PluralRules.Operand.v;

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


    /*
                LIST CONTROLLER
     */



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

    // Fetch wish list(paged)
    @RequestMapping(value = "/{userId}/wishlist/params", method = RequestMethod.GET)
    public ResponseEntity<List<WishEntity>> getWishListPaged(
            @PathVariable("userId") int userId,
            @RequestParam(value = "page",defaultValue = "0")Integer page,
            @RequestParam(value = "size",defaultValue = "5")Integer size) {
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

        PagedListHolder<WishEntity> pagedWishList= new PagedListHolder<>((List<WishEntity>)wishList);
        pagedWishList.setPage(page);
        pagedWishList.setPageSize(size);
        return new ResponseEntity<List<WishEntity>>(pagedWishList.getPageList(), HttpStatus.OK);
    }


    //fetch single game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}",method=RequestMethod.GET)
    public ResponseEntity<WishEntity> getOneWish(@PathVariable("userId")int userid,
                                                 @PathVariable("gameId")int gameid){
        System.out.println("fetch single game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        List<WishEntity> wishlist=wishRepo.findByUserAndGame(user,game);

        boolean isAvailable=false;
        Iterator<WishEntity> iter=wishlist.iterator();
        WishEntity wish=new WishEntity();
        while(iter.hasNext()){
            wish =iter.next();
            if(wish.getStatus()==1){
                isAvailable=true;
                break;
            }
        }

        if(!isAvailable){
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
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        //find the game
        GameEntity game = gameRepo.findOne(gameid);
        if (game == null) {
            System.out.println("can't find game...");
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
            System.out.println("can't find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("can't find the game...");
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


    //senior mathch in wishlist
    @RequestMapping(value="{userid}/wishlist/match/params",method=RequestMethod.POST)
    public ResponseEntity<List<WishListMatchResultItem>> GetWishListManyToManyMatch(@RequestBody ManyToManyTradeJsonItem YouWantGames,
                                                                                    @PathVariable("userid")int userid,
                                                                                    @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                                                    @RequestParam(value = "size",defaultValue = "5")Integer size){
        System.out.println("match the games in wishlist");
        matchAssist assist=new matchAssist();

        System.out.println(YouWantGames.getAddressId());

        int pointRange=YouWantGames.getPointRange();

        System.out.println(pointRange);

        List<Integer> YouWantGameList = assist.getGameIdList(YouWantGames.getYouWantGames());
        List<Integer> YouOfferGameList = assist.getGameIdList(YouWantGames.getYouOfferGames());

        int Wantsum=0;
        for(int i =0;i<YouWantGameList.size();i++){
            Wantsum+=wishRepo.getWishPoints(userid,YouWantGameList.get(i));
        }
        List<Integer> OfferRange=new ArrayList<>();
        OfferRange.add(Wantsum-pointRange);
        OfferRange.add(Wantsum+pointRange);

        Map<Integer,List<Integer>> OfferGamesMap
        for(int i =0;i<YouWantGameList.size();i++){

        }

        return new ResponseEntity<List<WishListMatchResultItem>>(HttpStatus.OK);
    }



    /*
                    OFFER CONTROLLER
    */



    //fetch all the offer(paged)
    @RequestMapping(value="/{userId}/offerlist/params",method=RequestMethod.GET)
    public ResponseEntity<List<OfferEntity>> getAllOfferPaged(@PathVariable("userId")int userId,
                                                              @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                              @RequestParam(value = "size",defaultValue = "5")Integer size){
        System.out.println("fetch all the offering games...");

        UserEntity user=userRepo.findByUserIdAndFetchOfferlist(userId);
        if(user==null){
            System.out.println("can't find the user");
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

        //get paged
        PagedListHolder<OfferEntity> PagedOfferList=new PagedListHolder<>((List<OfferEntity>)offerlist);
        PagedOfferList.setPageSize(size);
        PagedOfferList.setPage(page);
        return new ResponseEntity<List<OfferEntity>>(PagedOfferList.getPageList(),HttpStatus.OK);
    }

    //fetch all the offer
    @RequestMapping(value="/{userId}/offerlist",method=RequestMethod.GET)
    public ResponseEntity<List<OfferEntity>> getAllOffer(@PathVariable("userId")int userId){
        System.out.println("fetch all the offering games...");

        UserEntity user=userRepo.findByUserIdAndFetchOfferlist(userId);
        if(user==null){
            System.out.println("can't find the user");
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

    //fetch single offer
    @RequestMapping(value="/{userId}/offerlist/{gameId}",method=RequestMethod.GET)
    public ResponseEntity<OfferEntity> getOneOffer(@PathVariable("userId")int userid,
                                                   @PathVariable("gameId")int gameid){
        System.out.println("fetch single game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);

        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerlist.iterator();
        OfferEntity offer=new OfferEntity();
        while(iter.hasNext()){
            offer =iter.next();
            if(offer.getStatus()==1){
                isAvailable=true;
                break;
            }
        }

        if(!isAvailable){
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<OfferEntity>(offer,HttpStatus.OK);
        }
    }


    //add offer game
    @RequestMapping(value="/{userId}/offerlist",method=RequestMethod.POST)
    public ResponseEntity<OfferEntity> addItemsToOfferList(@PathVariable("userId")int userid,
        @RequestBody OfferJsonItem offerGame){
        System.out.println("add game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<OfferEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(offerGame.getGameId());
        if(game==null){
            System.out.println("can't find the game...");
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
    @RequestMapping(value="/{userId}/offerlist/{gameId}/delete",method=RequestMethod.PUT)
    public ResponseEntity<Void> deleteItemFromOfferList(@PathVariable("userId")int userid,
                                                               @PathVariable("gameId")int gameid){
        System.out.println("delete game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("can't find game...");
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
    @RequestMapping(value="/{userId}/offerlist/{gameId}/modify",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyItemFromOfferList(@PathVariable("userId")int userid,
                                                        @PathVariable("gameId")int gameid,
                                                        @RequestBody ModifyOfferJsonItem modifyPoints){
        System.out.println("modify the points...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("can't find the game...");
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


    /*
            ADDRESS CONTROLLER
     */




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

    // fetch address(paged)
    @RequestMapping(value="/{userId}/address/params",method=RequestMethod.GET)
    public ResponseEntity<List<AddressEntity>> getAddressPaged(@PathVariable("userId")int userId,
                                                               @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",defaultValue = "5")Integer size){
        UserEntity user = userRepo.findByUserIdAndFetchAddresses(userId);
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

    //Add more address
    @RequestMapping(value="/{userid}/address",method=RequestMethod.POST)
    public ResponseEntity<AddressEntity> addAddress(@RequestBody createAddressJsonItem addressItem,
                                                    @PathVariable("userid")int userid){
        System.out.println("creating new address");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find the user");
            return new ResponseEntity<AddressEntity>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=new AddressEntity();
        address.setAddress(addressItem.getAddress());
        address.setPhone(addressItem.getPhone());
        address.setReceiver(addressItem.getReceiver());
        address.setRegion(addressItem.getRegion());
        address.setUser(user);
        addressRepo.saveAndFlush(address);

        return new ResponseEntity<AddressEntity>(address,HttpStatus.OK);
    }



    //modify address
    @RequestMapping(value="/{userid}/address/{addressid}",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyAddress(@RequestBody createAddressJsonItem addressItem,
                                                       @PathVariable("userid")int userid,
                                                       @PathVariable("addressid")int addressid){
        System.out.println("modify the address...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=addressRepo.findByUserAndId(user,addressid);
        if(address==null){
            System.out.println("can't find address");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        addressRepo.updateAddress(addressItem.getAddress(),addressItem.getPhone(),addressItem.getReceiver(),addressItem.getRegion(), addressid);

        return new ResponseEntity<Void>(HttpStatus.OK);
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
            System.out.println("user can't found");
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


    /*
            MATCH CONTROLLER
     */


    //match game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}/match",method=RequestMethod.GET)
    public ResponseEntity<List<ReceiverOrderItem>> matchWistList(@PathVariable("userId")int userid,
                                                            @PathVariable("gameId")int gameid){
        System.out.println("match game...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<ReceiverOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("can't find game...");
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

        Iterator<OfferEntity> iterOfferList=offerList.iterator();
        List<Integer> offerUserid=new ArrayList<>();
        while(iterOfferList.hasNext()){
            OfferEntity offerentity=iterOfferList.next();
            int UserId=offerentity.getOfferEntityPK().getUser().getUserId();
            if(UserId!=userid){
                offerUserid.add(UserId);
            }
        }

        System.out.println("the size of the list is "+offerUserid.size());

        List<Integer> sendingGame;
        List<ReceiverOrderItem> resultOrder=new ArrayList<>();
        for(int i =0;i<offerUserid.size();i++){
            sendingGame=wishRepo.getSameGame(offerUserid.get(i),userid,wantPoint);
            for(int j =0;j<sendingGame.size();j++){
                ReceiverOrderItem orderItem=new ReceiverOrderItem();
                orderItem.setGetGameId(gameid);
                orderItem.setOfferGameId(sendingGame.get(j));
                orderItem.setSenderId(offerUserid.get(i));
                resultOrder.add(orderItem);
            }
        }
        return new ResponseEntity<List<ReceiverOrderItem>>(resultOrder,HttpStatus.OK);
    }


    //confirm the match
    @RequestMapping(value="/{userid}/wishlist/{gameId}/match/confirm",method=RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmWishMatch(@RequestBody CreateOrderJsonItem orderItem,
                                                         @PathVariable("gameId")int gameid,
                                                         @PathVariable("userid")int userid){

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        UserEntity targetUser=userRepo.findOne(orderItem.getTargetUserId());
        if(targetUser==null){
            System.out.println("can't find user...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity sendGame=gameRepo.findOne(orderItem.getGameId());
        if(sendGame==null){
            System.out.println("can't find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity receiveGame=gameRepo.findOne(gameid);
        if(receiveGame==null){
            System.out.println("can't find game...");
            return new ResponseEntity<TradeOrderEntity>(HttpStatus.NOT_FOUND);
        }

        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        tradeOrder.setCreatetime(time);
        tradeOrder.setStatus(2);
        int orderId=1;
        if(tradeOrderRepo.findAll()!=null){
            orderId=tradeOrderRepo.getMaxId()+1;
        }
        tradeOrder.setTradeOrderId(orderId);
        tradeOrderRepo.saveAndFlush(tradeOrder);

        //create TradeGame
        AddressEntity address=addressRepo.findOne(orderItem.getAddressId());

        //create the send game order
        int tradeGameId;
        if(tradeGameRepo.findAll().isEmpty()){
            tradeGameId=1;
        }
        else {
            tradeGameId = tradeGameRepo.getMaxId() + 1;
        }

        //create the send game order
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
    @RequestMapping(value="/{userid}/offerlist/{gameid}/match",method=RequestMethod.GET)
    public ResponseEntity<List<SenderOrderItem>> matchOfferList(@PathVariable("userid")int userid,
                                                                @PathVariable("gameid")int gameid){
        System.out.println("match the game in offer list...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
        if(game==null){
            System.out.println("can't find game...");
            return new ResponseEntity<List<SenderOrderItem>>(HttpStatus.NOT_FOUND);
        }

        //get the wanted points
        int wantPoint=0;
        List<OfferEntity> offerList=offerRepo.findByUserAndGame(user,game);
        Iterator<OfferEntity> offerIter=offerList.iterator();
        while(offerIter.hasNext()){
            OfferEntity offer=offerIter.next();
            if(offer.getStatus()==1){
                wantPoint=offer.getPoints();
                break;
            }
        }

        List<WishEntity> wishList=wishRepo.getWishGame(wantPoint,gameid);
        List<Integer> wishListUserId=new ArrayList<>();
        Iterator<WishEntity> wishListIter=wishList.iterator();

        while(wishListIter.hasNext()) {
            WishEntity wish = wishListIter.next();
            int UserId = wish.getWishEntityPK().getUser().getUserId();
            if (UserId != userid) {
                wishListUserId.add(UserId);
            }
        }

        List<Integer> receivingGame;
        List<SenderOrderItem> resultOrder=new ArrayList<>();
        for(int i =0;i<wishListUserId.size();i++){
            receivingGame=offerRepo.getSameGame(userid,wishListUserId.get(i),wantPoint);
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
    public ResponseEntity<TradeOrderEntity> confirmOfferMatch(@RequestBody CreateOrderJsonItem orderItem,
                                                              @PathVariable("gameid")int gameid,
                                                              @PathVariable("userid")int userid){
        UserEntity user=userRepo.findOne(userid);

        UserEntity targetUser=userRepo.findOne(orderItem.getTargetUserId());

        GameEntity SendGame=gameRepo.findOne(gameid);

        GameEntity ReceiveGame=gameRepo.findOne(orderItem.getGameId());


        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        tradeOrder.setCreatetime(time);
        tradeOrder.setStatus(2);
        int orderId=1;
        if(tradeOrderRepo.findAll()!=null){
            orderId=tradeOrderRepo.getMaxId()+1;
        }
        tradeOrder.setTradeOrderId(orderId);
        tradeOrderRepo.saveAndFlush(tradeOrder);

        //create trade game entity
        AddressEntity address=addressRepo.findOne(orderItem.getAddressId());

        int tradeGameId;
        if(tradeGameRepo.findAll().isEmpty()){
            tradeGameId=1;
        }
        else {
            tradeGameId = tradeGameRepo.getMaxId() + 1;
        }
        //create send game order
        TradeGameEntity tradeGameOne=new TradeGameEntity();
        tradeGameOne.setTradeOrder(tradeOrder);
        tradeGameOne.setTradeGameId(tradeGameId);
        tradeGameOne.setFromAddress(address);
        tradeGameOne.setSender(user);
        tradeGameOne.setGame(SendGame);
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
        tradeGameTwo.setGame(ReceiveGame);
        tradeGameTwo.setToAddress(address);
        tradeGameRepo.saveAndFlush(tradeGameTwo);

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



   //fetch all the order
    @RequestMapping(value="/{userid}/order",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getAllOrders(@PathVariable("userid")int userid){
        System.out.println("fetch all orders");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

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

        List<TradeGameEntity> tradeGameList=tradeGameRepo.findAll();
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            int orderid=tradeGame.getTradeOrder().getTradeOrderId();
            int ReceiverId=tradeGame.getReceiver().getUserId();
            int SenderId=tradeGame.getSender().getUserId();
            for(int i =0;i<ShowResult.size();i++){
                if(orderid==ShowResult.get(i).getTradeOrderId()&&(ReceiverId==userid||SenderId==userid)){
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
                    temp.add(0,ShowGameItem);
                    ShowResult.get(i).setGameDetail(temp);
                    ShowResult.get(i).setUserStatus(1);
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

        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);

    }

    //fetch all orders (paged)
    @RequestMapping(value="/{userid}/order/params",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getAllOrdersPaged(@PathVariable("userid")int userid,
                                                                 @RequestParam(value = "size",defaultValue = "5")Integer size,
                                                                 @RequestParam(value = "page",defaultValue = "0")Integer page){
        System.out.println("fetch all orders");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

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

        List<TradeGameEntity> tradeGameList=tradeGameRepo.findAll();
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            int orderid=tradeGame.getTradeOrder().getTradeOrderId();
            int ReceiverId=tradeGame.getReceiver().getUserId();
            int SenderId=tradeGame.getSender().getUserId();
            for(int i =0;i<ShowResult.size();i++){
                if(orderid==ShowResult.get(i).getTradeOrderId()&&(ReceiverId==userid||SenderId==userid)){
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
                    temp.add(0,ShowGameItem);
                    ShowResult.get(i).setGameDetail(temp);
                    ShowResult.get(i).setUserStatus(1);
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

        PagedListHolder<ShowOrderItem> pagedShowResult=new PagedListHolder<>(ShowResult);
        pagedShowResult.setPageSize(size);
        pagedShowResult.setPage(page);
        return new ResponseEntity<List<ShowOrderItem>>(pagedShowResult.getPageList(),HttpStatus.OK);
    }

    //fetch unconfirmed order
    @RequestMapping(value="/{userid}/order/unconfirmed",method=RequestMethod.GET)
    public ResponseEntity<List<ShowOrderItem>> getUnconfirmOrders(@PathVariable("userid")int userid){
        System.out.println("fetch all orders");
        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
        }

        List<TradeOrderEntity> tradeOrderList=tradeOrderRepo.findAll();


        //create the general order
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

        List<TradeGameEntity> tradeGameList=tradeGameRepo.findAll();
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
                        ShowOrderGamesItem ShowGameItems = new ShowOrderGamesItem();
                        ShowGameItems.setFromAddress(tradeGame.getFromAddress());
                        ShowGameItems.setGameId(tradeGame.getGame().getGameId());
                        ShowGameItems.setReceiver(tradeGame.getReceiver());
                        ShowGameItems.setReceiverStatus(tradeGame.getReceiverStatus());
                        ShowGameItems.setSender(tradeGame.getSender());
                        ShowGameItems.setSenderStatus(tradeGame.getSenderStatus());
                        ShowGameItems.setStatus(tradeGame.getStatus());
                        ShowGameItems.setToAddress(tradeGame.getToAddress());
                        ShowGameItems.setTrackingNumber(tradeGame.getTrackingNumber());
                        ShowGameItems.setTradeGameId(tradeGame.getTradeGameId());

                        List<ShowOrderGamesItem> temp = ShowResult.get(i).getGameDetail();
                        temp.add(0, ShowGameItems);
                        ShowResult.get(i).setGameDetail(temp);
                        ShowResult.get(i).setUserStatus(1);
                    }
                }
            }
        }

        //remove those orders without the involve of user
        Iterator<ShowOrderItem> resultIter=ShowResult.iterator();
        while(resultIter.hasNext()){
            ShowOrderItem item=resultIter.next();
            if(item.getUserStatus()==0){
                resultIter.remove();
            }
        }
        return new ResponseEntity<List<ShowOrderItem>>(ShowResult,HttpStatus.OK);
    }



    //confirm the order
    @RequestMapping(value="/{userid}/order/{orderid}/confirm",method=RequestMethod.PUT)
    public ResponseEntity<Void> confirmOrder(@PathVariable("userid")int userid,
                                             @PathVariable("orderid")int orderid,
                                             @RequestBody ConfirmOrderJsonItem address){
        System.out.println("confirm the order...");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        TradeOrderEntity tradeOrder=tradeOrderRepo.findOne(orderid);
        if(tradeOrder==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<TradeGameEntity> tradeGameList=tradeGameRepo.findByOrderId(orderid);
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            //confirm as receiver
            if(tradeGame.getReceiver().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                AddressEntity Address=addressRepo.findOne(address.getAddressId());
                tradeGameRepo.ConfirmByReceiver(tradeGameId,Address);
                //get the status in trade or minus by one shows one game order is confirmed
                if(tradeGame.getStatus()==0){
                    tradeOrderRepo.confirmOneGame(orderid);
                }
                continue;
            }
            //confirm as sender
            else if(tradeGame.getSender().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                AddressEntity Address=addressRepo.findOne(address.getAddressId());
                tradeGameRepo.ConfirmBySender(tradeGameId,Address);
                if(tradeGame.getStatus()==0){
                    tradeOrderRepo.confirmOneGame(orderid);
                }
                continue;
            }
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    //refuse the order
    @RequestMapping(value="/{userid}/order/{orderid}/refuse",method=RequestMethod.PUT)
    public ResponseEntity<Void> refuseOrder(@PathVariable("userid")int userid,
                                             @PathVariable("orderid")int orderid){
        System.out.println("refusing order...");

        UserEntity user=userRepo.getOne(userid);
        if(user==null){
            System.out.println("can't find user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        TradeOrderEntity tradeOrder=tradeOrderRepo.findOne(orderid);
        if(tradeOrder==null){
            System.out.println("can't find order...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<TradeGameEntity> tradeGameList=tradeGameRepo.findByOrderId(orderid);
        Iterator<TradeGameEntity> iter=tradeGameList.iterator();
        while(iter.hasNext()){
            TradeGameEntity tradeGame=iter.next();
            //check the game order as receiver
            if(tradeGame.getReceiver().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                tradeGameRepo.RefuseByReceiver(tradeGameId);
                continue;
            }
            //check game order as sender
            else if(tradeGame.getSender().getUserId()==userid){
                int tradeGameId=tradeGame.getTradeGameId();
                tradeGameRepo.RefuseBySender(tradeGameId);
                continue;
            }
        }
        //update the status in trade order
        tradeOrderRepo.cancelOrder(orderid);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}