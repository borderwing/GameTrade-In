package com.example.ye.gametrade_in;

import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.MatchBean;
import com.example.ye.gametrade_in.Bean.MyListBean;
import com.example.ye.gametrade_in.Bean.OfferListBean;
import com.example.ye.gametrade_in.Bean.UserBean;
import com.example.ye.gametrade_in.Bean.UserDetailBean;
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

    // Get my list detail list Bean
    public MyListBean[] GetMyListBean(String myListDetailString){
        Gson gson = new Gson();
        MyListBean[] myList = gson.fromJson(myListDetailString, MyListBean[].class);
        return myList;
    }

    // Get my list detail single Bean
    public MyListBean GetMyListSingleBean(String myListDetailString){
        Gson gson = new Gson();
        MyListBean myList = gson.fromJson(myListDetailString, MyListBean.class);
        return myList;
    }

    // Get match list Bean
    public MatchBean[] GetMatchBean(String matchDetailedString){
        Gson gson = new Gson();
        MatchBean[] match = gson.fromJson(matchDetailedString, MatchBean[].class);
        return match;
    }

    // Get address list Bean
    public AddressBean[] GetAddressListBean(String addressDetailedString){
        Gson gson = new Gson();
        AddressBean[] addressList = gson.fromJson(addressDetailedString, AddressBean[].class);
        return addressList;
    }

    // Get address Bean
    public AddressBean GetAddressSingleBean(String addressDetailedString){
        Gson gson = new Gson();
        AddressBean addressList = gson.fromJson(addressDetailedString, AddressBean.class);
        return addressList;
    }



    // Get my offer list detail list Bean
    public OfferListBean[] GetMyOfferListBean(String myListDetailString){
        Gson gson = new Gson();
        OfferListBean[] myOfferList = gson.fromJson(myListDetailString, OfferListBean[].class);
        return myOfferList;
    }



    // Get user detail Bean
    public UserDetailBean GetUserDetailBean(String userDetailString){
        Gson gson = new Gson();
        UserDetailBean userDetail = gson.fromJson(userDetailString, UserDetailBean.class);
        return userDetail;
    }

    // Get gameTile list Bean
    public GameTileBean[] GetGameTileListBean(String gameTileDetail){
        Gson gson = new Gson();
        GameTileBean[] gameTile = gson.fromJson(gameTileDetail, GameTileBean[].class);
        return gameTile;
    }
}
