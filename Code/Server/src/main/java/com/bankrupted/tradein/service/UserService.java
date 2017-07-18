package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepo;


    public UserEntity getUserById(int userid){
        UserEntity user=userRepo.findOne(userid);
        return user;
    }


    public UserEntity getUserByIdAndFetchWishList(int userid){
        UserEntity user=userRepo.findByUserIdAndFetchWishlist(userid);
        return user;
    }


    public UserEntity getUserByIdAndFetchOfferList(int userid){
        UserEntity user=userRepo.findByUserIdAndFetchOfferlist(userid);
        return user;
    }

    public UserEntity getUserByIdAndFetchAddress(int userid){
        UserEntity user=userRepo.findByUserIdAndFetchAddresses(userid);
        return user;
    }
}
