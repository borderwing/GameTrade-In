package com.example.ye.gametrade_in.Bean;

import java.io.Serializable;

/**
 * Created by ye on 2017/7/12.
 */

public class AddressBean implements Serializable {


    public Integer addressId;
    public String receiver;
    public String phone;
    public String region;
    public String address;



    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
