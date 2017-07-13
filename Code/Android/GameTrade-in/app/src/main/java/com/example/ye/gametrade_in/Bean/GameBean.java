package com.example.ye.gametrade_in.Bean;

/**
 * Created by ye on 2017/7/4.
 */

public class GameBean {
    public Integer gameId;
    public String title;
    public String platform;
    public String language;
    public String genre;
    public Integer evaluatePoint;
    public String wishes;
    public String offers;
    public String tradeGames;

    @Override
    public String toString(){
        return "gameID:"+ gameId +" title:"+ title +" platform:" + platform + " language:" + language + " genre:" + genre + " evaluatePoint" + evaluatePoint + " wishes:" + wishes + " offers:" + offers
                + " tradeGames:" + tradeGames;
    }
}
