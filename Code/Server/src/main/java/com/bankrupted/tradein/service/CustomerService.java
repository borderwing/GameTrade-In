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
        customer=customerRepo.getOne(customerid);
        return customer;
    }

}
