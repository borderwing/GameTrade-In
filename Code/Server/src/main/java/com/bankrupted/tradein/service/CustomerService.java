package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.CustomerEntity;
import com.bankrupted.tradein.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepo;

    public CustomerEntity getCustomerById(int customerid){
        CustomerEntity customer=new CustomerEntity();
        try {
            customer = customerRepo.getOne(customerid);
        } catch (ClassCastException cce) {
            customer = null;
        }
        return customer;
    }

    public void UpdateRating(int targetUserId,int rating){
        CustomerEntity customer=customerRepo.findOne(targetUserId);
        int ratingNum;
        if(customer.getRatingUserNum()==null){
            ratingNum=0;
        }
        else{
            ratingNum=customer.getRatingUserNum();
        }
        int newRating=((customer.getRating()*ratingNum)+rating)/(ratingNum+1);
        customerRepo.updateRating(newRating,targetUserId);
    }
}
