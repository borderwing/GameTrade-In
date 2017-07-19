package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.assist.matchAssist;
import com.bankrupted.tradein.assist.matchAssist.*;
import com.bankrupted.tradein.model.*;
import com.bankrupted.tradein.model.json.*;
import com.bankrupted.tradein.model.temporaryItem.*;
import com.bankrupted.tradein.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankrupted.tradein.repository.*;

import java.lang.annotation.Target;
import java.util.*;
import java.sql.Timestamp;

import static org.python.icu.text.PluralRules.Operand.t;
import static org.python.icu.text.PluralRules.Operand.v;

/**
 * Created by lykav on 2017/6/29.
 */
@RestController
@RequestMapping("/api/user")
public class               UserController {

    private String apiPath = "/api/user";


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

        GameEntity game=gameRepo.findOne(gameid);
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
            @RequestBody WishJsonItem wishJsonItem) {

        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game = gameRepo.findOne(wishJsonItem.getGameId());
        if (game == null) return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);

        Timestamp time=new Timestamp(System.currentTimeMillis());

        WishEntity wish=wishService.getOneWishByUserAndGame(user,game);

        //the game is not in the list
        if(wish==null){
            WishEntity NewWish=wishService.saveWishInAdd(wishJsonItem,user,game,time);
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
        GameEntity game = gameRepo.findOne(gameid);
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
            @RequestBody ModifyWishJsonItem modifyItem){
        System.out.println("modifying the points...");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find the user...");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        GameEntity game=gameRepo.findOne(gameid);
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
    public ResponseEntity<List<WishListMatchResultItem>> GetWishListManyToManyMatch(@RequestBody ManyToManyTradeJsonItem YouWantGames,
                                                                                    @PathVariable("userid")int userid,
                                                                                    @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                                                    @RequestParam(value = "size",defaultValue = "5")Integer size){
        System.out.println("match the games in wishlist");

        matchAssist assist=new matchAssist();

        int pointRange=YouWantGames.getPointRange();

        List<Long> YouWantGameList = assist.getGameIdList(YouWantGames.getYouWantGames());
        List<Long> YouOfferGameList = assist.getGameIdList(YouWantGames.getYouOfferGames());

        int Wantsum=0;
        for(int i =0;i<YouWantGameList.size();i++){
            Wantsum+=wishRepo.getWishPoints(userid,YouWantGameList.get(i));
        }
        List<Integer> OfferRange=new ArrayList<>();
        OfferRange.add(Wantsum-pointRange);
        OfferRange.add(Wantsum+pointRange);

        //get the people who owns the game the customer want
        List<OfferEntity> OfferWantedGameList=offerRepo.findAllExceptById(userid);
        Map<Integer,Integer> AvailablePersonMap = assist.getAvailablePerson(OfferWantedGameList,YouWantGameList);

        //check whether the points is in range
        List<Integer> targetUserId=new ArrayList<>();
        Iterator<Map.Entry<Integer,Integer>> iter=AvailablePersonMap.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<Integer,Integer> userPoints=iter.next();
            int points=userPoints.getValue();
            if(points>OfferRange.get(0)&&points<OfferRange.get(1)){
                targetUserId.add(userPoints.getKey());
            }
        }


        List<OfferEntity> UserOffer=offerRepo.findById(userid);
        Map<Long,Integer> UserOfferPoints=new HashMap<>();
        for(int i =0;i<UserOffer.size();i++){
            UserOfferPoints.put(UserOffer.get(i).getOfferEntityPK().getGame().getGameId(),UserOffer.get(i).getPoints());
            System.out.println(UserOffer.get(i).getOfferEntityPK().getGame().getGameId());
        }
        List<WishListMatchResultItem> result=new ArrayList<>();
        for(int i =0 ;i<targetUserId.size();i++){
            int TargetUserId = targetUserId.get(i);
            Map<Long,Integer> TargetUserWishPoints=new HashMap<>();
            List<WishEntity> TargetUserWishList=wishRepo.findByUserId(TargetUserId);
            for(int j =0;j< TargetUserWishList.size();j++){
                TargetUserWishPoints.put(TargetUserWishList.get(j).getWishEntityPK().getGame().getGameId(), TargetUserWishList.get(j).getPoints());
            }
            List<String> PotentialOfferGames=assist.getOfferGames(UserOfferPoints,TargetUserWishPoints,OfferRange.get(0),OfferRange.get(1));

            Iterator<String> StringIter=PotentialOfferGames.iterator();
            while(StringIter.hasNext()){
                String offerGame=StringIter.next();
                WishListMatchResultItem resultItem = new WishListMatchResultItem();
                resultItem.setTargetUserId(TargetUserId);
                resultItem.setYouOfferGame(offerGame);
                resultItem.setYouWantGame(YouWantGames.getYouWantGames());
                result.add(resultItem);
            }
        }
        return new ResponseEntity<List<WishListMatchResultItem>>(result,HttpStatus.OK);
    }


    //confirm the senior match
    @RequestMapping(value="{userid}/wishlist/match/confirm",method = RequestMethod.POST)
    public ResponseEntity<TradeOrderEntity> confirmMatch(@RequestBody MatchConfirmJsonItem matchInfo,
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

        for(int i = 0;i<youOfferGameIdList.size();i++){
            GameEntity game=gameRepo.findOne(youOfferGameIdList.get(i));
            TradeGameEntity tradeGame=orderService.setSenderTradeGame(address,user,game, TargetUser,orderid);
            tradeGameList.add(tradeGame);
        }

        for(int i = 0;i<youWantGameIdList.size();i++){
            GameEntity game=gameRepo.findOne(youWantGameIdList.get(i));
            TradeGameEntity tradeGame=orderService.setReceiverTradeGame(address,user,game,TargetUser,orderid);
            tradeGameList.add(tradeGame);
        }
        tradeOrder.setTradeGames(tradeGameList);

        return new ResponseEntity<TradeOrderEntity>(tradeOrder,HttpStatus.OK);
    }


    /*//triple user match
    @RequestMapping(value="{userid}/wishlist/match/senior",method=RequestMethod.POST)
    public ResponseEntity<>*/
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

        GameEntity game=gameRepo.findOne(gameid);
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
        @RequestBody OfferJsonItem offerGame){
        System.out.println("add game...");

        UserEntity user=userService.getUserById(userid);
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

        GameEntity game=gameRepo.findOne(gameid);
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
    public ResponseEntity<AddressEntity> addAddress(@RequestBody createAddressJsonItem addressItem,
                                                    @PathVariable("userid")int userid){
        System.out.println("creating new address");

        UserEntity user=userRepo.findOne(userid);
        if(user==null){
            System.out.println("can't find the user");
            return new ResponseEntity<AddressEntity>(HttpStatus.NOT_FOUND);
        }

        AddressEntity address=addressService.addNewAddress(addressItem,user);

        return new ResponseEntity<AddressEntity>(address,HttpStatus.OK);
    }



    //modify address
    @RequestMapping(value="/{userid}/address/{addressid}",method=RequestMethod.PUT)
    public ResponseEntity<Void> modifyAddress(@RequestBody createAddressJsonItem addressItem,
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



  /*  //Create Game to pending game
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
*/

    /*
            MATCH CONTROLLER
     */


    //match game in wish list
    @RequestMapping(value="/{userId}/wishlist/{gameId}/match",method=RequestMethod.GET)
    public ResponseEntity<List<ReceiverOrderItem>> matchWistList(@PathVariable("userId")int userid,
                                                            @PathVariable("gameId")long gameid){
        System.out.println("match game...");

        UserEntity user=userService.getUserById(userid);
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
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==1){
                wantPoint=wish.getPoints();
                break;
            }
        }

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

        List<Long> sendingGame;
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
        int orderId=orderService.getNewOrderId();
        orderService.saveTradeOrder(tradeOrder,time,2,orderId);

        //create TradeGame
        AddressEntity address=addressRepo.findOne(orderItem.getAddressId());

        //create the send game order
        TradeGameEntity tradeGameOne=orderService.setSenderTradeGame(address,user,sendGame,targetUser,orderId);

        //create the receive game order
        TradeGameEntity tradeGameTwo=orderService.setReceiverTradeGame(address,user,receiveGame,targetUser,orderId);

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
                                                                @PathVariable("gameid")long gameid){
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

        List<Long> receivingGame;
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
                                                              @PathVariable("gameid")long gameid,
                                                              @PathVariable("userid")int userid){
        UserEntity user=userService.getUserById(userid);

        UserEntity targetUser=userService.getUserById(orderItem.getTargetUserId());

        GameEntity SendGame=gameRepo.findOne(gameid);

        GameEntity ReceiveGame=gameRepo.findOne(orderItem.getGameId());


        //create TradeOrder
        TradeOrderEntity tradeOrder=new TradeOrderEntity();
        Timestamp time=new Timestamp(System.currentTimeMillis());
        int orderId=orderService.getNewOrderId();
        orderService.saveTradeOrder(tradeOrder,time,2,orderId);

        //create trade game entity
        AddressEntity address=addressRepo.findOne(orderItem.getAddressId());

        //create send game order
        TradeGameEntity tradeGameOne=orderService.setSenderTradeGame(address,user,SendGame,targetUser,orderId);

        //create the receive game order
        TradeGameEntity tradeGameTwo=orderService.setReceiverTradeGame(address,user,ReceiveGame,targetUser,orderId);

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
    public ResponseEntity<List<ShowOrderItem>> getAllOrdersPaged(@PathVariable("userid")int userid,
                                                                 @RequestParam(value = "size",defaultValue = "5")Integer size,
                                                                 @RequestParam(value = "page",defaultValue = "0")Integer page){
        System.out.println("fetch all orders");

        UserEntity user=userService.getUserById(userid);
        if(user==null){
            System.out.println("can't find order...");
            return new ResponseEntity<List<ShowOrderItem>>(HttpStatus.NOT_FOUND);
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


}