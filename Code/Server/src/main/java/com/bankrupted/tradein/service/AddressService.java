package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.AddressEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.json.createAddressJsonItem;
import com.bankrupted.tradein.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class AddressService {
    @Autowired
    AddressRepository addressRepo;

    public AddressEntity addNewAddress(createAddressJsonItem addressItem,UserEntity user){
        AddressEntity address=new AddressEntity();
        address.setAddress(addressItem.getAddress());
        address.setPhone(addressItem.getPhone());
        address.setReceiver(addressItem.getReceiver());
        address.setRegion(addressItem.getRegion());
        address.setUser(user);
        addressRepo.saveAndFlush(address);
        return address;
    }

    public AddressEntity getAddressByUserAndId(UserEntity user,int addressid){
        AddressEntity address=addressRepo.findByUserAndId(user,addressid);
        return address;
    }

    public void updateAddress(createAddressJsonItem addressItem,int addressid){
        addressRepo.updateAddress(addressItem.getAddress(),addressItem.getPhone(),addressItem.getReceiver(),addressItem.getRegion(), addressid);
    }
}
