package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.repository.CustomerRepository;
import com.bankrupted.tradein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    CustomerRepository customerRepo;


    public UserEntity getUserById(int userid){
        return userRepo.findOne(userid);
    }

    public UserEntity getUserByIdAndFetchWishList(int userid){
        return userRepo.findByUserIdAndFetchWishlist(userid);
    }

    public UserEntity getUserByIdAndFetchOfferList(int userid){
        return userRepo.findByUserIdAndFetchOfferlist(userid);
    }

    public List<Integer> getAllUserId(){
        List<Integer> UserId=new ArrayList<>();
        return userRepo.getAllUserId();
    }

    public UserEntity getUserByIdAndFetchAddress(int userid){
        return userRepo.findByUserIdAndFetchAddresses(userid);
    }

    public Integer getUserRatingById(int userid){
        return customerRepo.getRatingById(userid);
    }

    public Integer getRaingUserNumById(int userid){
        return customerRepo.getRatingUserNumById(userid);
    }

    public UserEntity getUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public List<UserEntity> getAllUser(){
        return userRepo.findAll();
    }
}
