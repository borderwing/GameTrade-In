package com.example.ye.gametrade_in;

import com.google.gson.Gson;

public class JSONProcessor {
    public String gameDetailJsonToGameBean(String gameDetailString){
        Gson gson = new Gson();
        GameBean game = gson.fromJson(gameDetailString, GameBean.class);
        return game.toString();
    }

    public GameBean GetGameBean(String gameDetailString){
        Gson gson = new Gson();
        GameBean game = gson.fromJson(gameDetailString, GameBean.class);
        return game;
    }
}
