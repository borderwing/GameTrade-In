package com.example.ye.gametrade_in.Bean;



public class UserBean {
    public Integer userId ;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString(){
        return "userId" + userId;
    }
}
