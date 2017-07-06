package controller;

import model.json.LoginJsonItem;
import model.UserEntity;
import model.json.ReturnLoginJsonItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import repository.UserRepository;

import java.util.List;
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
        if(user==null){
            //cant find the user
            System.out.println("not find user");
            return new ResponseEntity<ReturnLoginJsonItem>(HttpStatus.NOT_FOUND);
        }
        if(user.getPassword().equals(loginItem.getPassword())){
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
