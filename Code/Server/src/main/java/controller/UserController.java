package controller;

import model.*;
import model.json.*;
import model.temporaryItem.ReceiverOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.*;

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
    @RequestMapping(value="/{userId}/offer/{gameId}/match",method=RequestMethod.GET)
    public ResponseEntity<ReceiverOrderItem> matchOfferList(@PathVariable("userId")int userid,
                                                            @PathVariable("gameId")int gameid){
        System.out.println("match game...");

        ReceiverOrderItem receiveOrederItemlist=new ReceiverOrderItem();

        UserEntity user=userRepo.findOne(userid);

        GameEntity game=gameRepo.findOne(gameid);


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

        int offerUserId=0;
        Iterator<OfferEntity> offerIter=offerList.iterator();
        List<Integer> offerUserid=null;
        while(iter.hasNext()){
            OfferEntity offer=offerIter.next();
            if(offer.getOfferEntityPK().getUser().getUserId()==offerUserId){
                continue;
            }
            else{
                offerUserid.add(offer.getOfferEntityPK().getUser().getUserId());
            }
        }

        //匹配成立情况  用户的offer 和能提供用户的wishlist 笛卡尔集
        for(int i =0;i<offerUserid.size();i++){
            List<>
        }

    }
}