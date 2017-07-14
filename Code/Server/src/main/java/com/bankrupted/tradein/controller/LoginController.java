package com.bankrupted.tradein.controller;

import com.bankrupted.tradein.model.json.LoginJsonItem;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.json.ReturnLoginJsonItem;
import com.bankrupted.tradein.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.bankrupted.tradein.repository.UserRepository;

/**
 * Created by homepppp on 2017/6/30.
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    UserRepository userrepository;

    @RequestMapping(value="/",method= RequestMethod.POST)
    public ResponseEntity<ReturnLoginJsonItem> checkLogin(@RequestBody LoginJsonItem loginItem){
        System.out.println("confirm the username");
        UserEntity user=userrepository.findByUsername(loginItem.getUsername());
        System.out.println(userrepository.getMaxId());
        if(user==null){
            //cant find the user
            System.out.println("not find user");
            return new ResponseEntity<ReturnLoginJsonItem>(HttpStatus.NOT_FOUND);
        }

        String encryptedPassword = SecurityUtility.passwordEncoder().encode(loginItem.getPassword());

        if(user.getPassword().equals(encryptedPassword)){
            ReturnLoginJsonItem returnLogin=new ReturnLoginJsonItem();
            returnLogin.setUserId(user.getUserId());
            return new ResponseEntity<ReturnLoginJsonItem>(returnLogin,HttpStatus.OK);
        }

        else {
            //wrong password
            System.out.println("password wrong");
            return new ResponseEntity<ReturnLoginJsonItem>(HttpStatus.NOT_FOUND);
        }
    }
}
