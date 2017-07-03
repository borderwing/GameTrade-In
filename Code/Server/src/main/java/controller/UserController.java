package controller;

import model.*;
import model.json.CreateGameJsonItem;
import model.json.LoginJsonItem;
import model.json.RegisterJsonItem;
import model.json.WishJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.*;

import javax.validation.constraints.Null;
import javax.ws.rs.Path;
import java.util.*;

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
        return new ResponseEntity<List<WishEntity>>((List<WishEntity>) wishList, HttpStatus.OK);
    }

    // add items to wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.POST)
    public ResponseEntity<WishEntity> addItemsToWishList(
            @PathVariable("userId") int userId,
            @RequestBody WishJsonItem wishJsonItem
    ) {
        if (wishJsonItem == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.OK);
        }
        UserEntity user = userRepo.findOne(userId);
        if (user == null) {
            return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);
        }

        GameEntity game = gameRepo.findOne(wishJsonItem.getGameId());
        if (game == null) return new ResponseEntity<WishEntity>(HttpStatus.NOT_FOUND);

        WishEntityPK wishEntityPK = new WishEntityPK();
        wishEntityPK.setUser(user);
        wishEntityPK.setGame(game);

        WishEntity wish = wishRepo.findOne(wishEntityPK);

        if (wish != null) {
            // user have already added this game to her wish list
            System.out.println("user " + user.getUserId() + " has already added game " +
                    game.getGameId() + " in her wish list.");
            return new ResponseEntity<WishEntity>(HttpStatus.CONFLICT);
        }

        wish = new WishEntity();
        wish.setPoints(wishJsonItem.getPoints());
        wish.setStatus(wishJsonItem.getStatus());
        wish.getWishEntityPK().setUser(user);
        wish.getWishEntityPK().setGame(game);


        wish = wishRepo.saveAndFlush(wish);

        System.out.println("added game " + game.getGameId() + " to user " +
                user.getUserId() + "\'s wish list.");


        return new ResponseEntity<WishEntity>(wish, HttpStatus.OK);
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
}
