package com.example.ye.gametrade_in;

import com.google.gson.Gson;

public class JSONProcessor {

    // Get game Bean
    public GameBean GetGameBean(String gameDetailString){
        Gson gson = new Gson();
        GameBean game = gson.fromJson(gameDetailString, GameBean.class);
        return game;
    }

    // Get user Bean
    public UserBean GetUserBean(String userDetailString){
        Gson gson = new Gson();
        UserBean user = gson.fromJson(userDetailString, UserBean.class);
        return user;
    }

    // Get user detail Bean
    public UserDetailBean GetUserDetailBean(String userDetailString){
        Gson gson = new Gson();
        UserDetailBean userDetail = gson.fromJson(userDetailString, UserDetailBean.class);
        return userDetail;
    }
}
