package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.model.CustomerEntity;
import com.bankrupted.tradein.model.RoleEntity;
import com.bankrupted.tradein.model.json.RegisterJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.bankrupted.tradein.repository.CustomerRepository;
import com.bankrupted.tradein.repository.RoleRepository;
import com.bankrupted.tradein.repository.UserRepository;
import com.bankrupted.tradein.utility.SecurityUtility;

import java.util.ArrayList;
import java.util.Collection;

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
    @Autowired
    RoleRepository roleRepo;

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
        CustomerEntity customer = new CustomerEntity();

        customer.setEmail(registerItem.getEmail());
        customer.setPhone(registerItem.getPhone());
        customer.setUsername(registerItem.getUsername());

        // encrypt the password and persist it
        String encryptedPassword = SecurityUtility.passwordEncoder().encode(registerItem.getPassword());
        customer.setPassword(encryptedPassword);

        customer.setRating(0);

        Collection<RoleEntity> roles = new ArrayList<>();
        RoleEntity customerRole = roleRepo.findByName(RoleRepository.customerRoleName);
        if(customerRole != null)  roles.add(customerRole);

        customer = customerRepo.saveAndFlush(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(customer.getUserId()).toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
