package com.example.ye.gametrade_in;

import com.google.gson.Gson;

public class JSONProcessor {
    public GameBean GetGameBean(String gameDetailString){
        Gson gson = new Gson();
        GameBean game = gson.fromJson(gameDetailString, GameBean.class);
        return game;
    }

    public UserBean GetUserBean(String userDetailString){
        Gson gson = new Gson();
        UserBean user = gson.fromJson(userDetailString, UserBean.class);
        return user;
    }
}
