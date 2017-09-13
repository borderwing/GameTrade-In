package com.example.ye.gametrade_in.Bean.temp;

/**
 * Created by ye on 2017/7/12.
 */

public class CreateOrderBean {

    public Long gameId ;
    public Long targetUserId;
    public Long addressId;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public CreateOrderBean(Long gameId, Long targetUserId, Long addressId) {
        this.gameId = gameId;
        this.targetUserId = targetUserId;
        this.addressId = addressId;
    }
}
