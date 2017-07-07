package com.example.ye.gametrade_in;

import android.app.Application;

/**
 * Created by ye on 2017/7/7.
 */

public class GameTradeInApplication extends Application {
    public String appVersion = "v1.0";

    private UserBean loginUser = new UserBean();

    public UserBean GetLoginUser(){
        return loginUser;
    }

    public void SetUserLogin(UserBean user){
        loginUser.setUserId(user.getUserId());
    }

    public void SetUserLogout(){
        loginUser = new UserBean();
    }

}
