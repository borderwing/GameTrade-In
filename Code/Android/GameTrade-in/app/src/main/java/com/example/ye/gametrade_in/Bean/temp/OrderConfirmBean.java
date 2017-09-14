package com.example.ye.gametrade_in.Bean.temp;

/**
 * Created by lykav on 9/13/2017.
 */

public class OrderConfirmBean {
    private int addressId;

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public OrderConfirmBean(int addressId) {
        this.addressId = addressId;
    }
}
