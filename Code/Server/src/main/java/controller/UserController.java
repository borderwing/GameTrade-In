package controller;

import model.*;
import model.json.WishItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.CustomerRepository;
import repository.GameRepository;
import repository.UserRepository;
import repository.WishRepository;

import java.util.*;

import static java.lang.System.in;

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

    // Retrieve Single User
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUser(@PathVariable("id") int id){
        System.out.println("Fetching User with id " + id);
        UserEntity user = userRepo.findOne(id);
        if(user == null){
            System.out.println("Cannot find User with id " + id);
            return new ResponseEntity<UserEntity>(HttpStatus.NOT_FOUND);
        }

        CustomerEntity customer = customerRepo.findOne(user.getUserId());
        if(customer != null){
            return new ResponseEntity<UserEntity>(customer, HttpStatus.OK);
        }

        return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
    }

    // Create a User
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody UserEntity user, UriComponentsBuilder ucBuilder){
        System.out.println("Creating User...");

        if(userRepo.findByUsername(user.getUsername()) != null){
            System.out.println("A User with username \"" + user.getUsername() + "\" already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        userRepo.saveAndFlush(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path(apiPath + "/{id}").buildAndExpand(user.getUserId()).toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Fetch wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.GET)
    public ResponseEntity<List<WishEntity>> getWishList(
            @PathVariable("userId")int userId){
        UserEntity user = userRepo.findByUserIdAndFetchWishlist(userId);
        if(user == null){
            System.out.println("Cannot find User with id " + userId);
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }
        Collection<WishEntity> wishList = user.getWishes();
        return new ResponseEntity<List<WishEntity>>((List<WishEntity>)wishList, HttpStatus.OK);
    }

    // add items to wish list
    @RequestMapping(value = "/{userId}/wishlist", method = RequestMethod.POST)
    public ResponseEntity<List<WishEntity>> addItemsToWishList(
            @PathVariable("userId") int userId,
            @RequestBody List<WishItem> wishItems
    ){
        if(wishItems == null){
            return new ResponseEntity<List<WishEntity>>(HttpStatus.OK);
        }
        UserEntity user = userRepo.findOne(userId);
        if(user == null){
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }

        List<WishEntity> addedWishes = new Vector<WishEntity>();

        for(WishItem requestItem : wishItems){
            GameEntity game = gameRepo.findOne(requestItem.getGameId());
            if(game == null)  continue;

            WishEntityPK wishEntityPK = new WishEntityPK();
            wishEntityPK.setUser(user);
            wishEntityPK.setGame(game);

            WishEntity wish = wishRepo.findOne(wishEntityPK);

            if(wish != null){
                // user have already added this game to her wish list
                System.out.println("user " + user.getUserId() + " has already added game " +
                                    game.getGameId() + " in her wish list.");
                continue;
            }

            wish = new WishEntity();
            wish.setPoints(requestItem.getPoints());
            wish.setStatus(requestItem.getStatus());
            wish.getWishEntityPK().setUser(user);
            wish.getWishEntityPK().setGame(game);


            wish = wishRepo.saveAndFlush(wish);
            addedWishes.add(wish);

            System.out.println("added game " + game.getGameId() + " to user " +
                               user.getUserId() + "\'s wish list.");
        }

        return new ResponseEntity<List<WishEntity>>((List<WishEntity>)addedWishes, HttpStatus.OK);
    }

}
