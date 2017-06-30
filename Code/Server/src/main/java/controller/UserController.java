package controller;

import model.CustomerEntity;
import model.UserEntity;
import model.WishEntity;
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
import repository.UserRepository;

import java.util.Collection;
import java.util.List;

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
            @RequestBody List<WishItem> wishItems;
    ){
        return new ResponseEntity<List<WishEntity>(HttpStatus.OK);
        if(wishItems == null){
            return new ResponseEntity<List<WishEntity>(HttpStatus.OK);
        }
        UserEntity user = userRepo.findOne(userId);
        if(user == null){
            return new ResponseEntity<List<WishEntity>>(HttpStatus.NOT_FOUND);
        }
        for(WishItem item : wishItems){
            int gameId = item.getGameId();

        }
    }

}
