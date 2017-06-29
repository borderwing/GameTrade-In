package controller;

import model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import repository.UserRepository;

/**
 * Created by lykav on 2017/6/29.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private String apiPath = "/api/user";

    @Autowired
    UserRepository userRepo;

    // Retrieve Single User
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> getUser(@PathVariable("id") int id){
        System.out.println("Fetching User with id " + id);
        UserEntity user = userRepo.findOne(id);
        if(user == null){
            System.out.println("Cannot find User with id " + id);
            return new ResponseEntity<UserEntity>(HttpStatus.NOT_FOUND);
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



}
