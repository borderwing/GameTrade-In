package com.bankrupted.tradein.model.json.user;

/**
 * Created by homepppp on 2017/7/6.
 */
public class CreateAddressJson {
    private String receiver;
    private String phone;
    private String address;
    private String region;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
