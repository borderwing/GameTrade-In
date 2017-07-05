package controller;

import model.CustomerEntity;
import model.json.RegisterJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import repository.CustomerRepository;
import repository.UserRepository;

/**
 * Created by homepppp on 2017/7/3.
 */

@RestController
@RequestMapping("/api")
public class RegisterController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    CustomerRepository customerRepo;

    // Create a User
    @RequestMapping(value = "/register/", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody RegisterJsonItem registerItem, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating User...");

        //check whether the username is duplicated
        if (userRepo.findByUsername(registerItem.getUsername()) != null) {
            System.out.println("A User with username \"" + registerItem.getUsername() + "\" already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        //create a new user
        CustomerEntity customer=new CustomerEntity();
        int id=userRepo.getMaxId()+1;
        customer.setUserId(id);
        customer.setEmail(registerItem.getEmail());
        customer.setPhone(registerItem.getPhone());
        customer.setUsername(registerItem.getUsername());
        customer.setRating(0);
        customer.setRole(0);
        customerRepo.saveAndFlush(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(customer.getUserId()).toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
