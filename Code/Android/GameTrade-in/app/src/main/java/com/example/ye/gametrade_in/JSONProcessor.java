package com.example.ye.gametrade_in;

import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.MatchBean;
import com.example.ye.gametrade_in.Bean.MyListBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.Bean.UserLoginBean;
import com.example.ye.gametrade_in.Bean.UserDetailBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONProcessor {
    Gson gson = new Gson();

    // Get game Bean
    public GameBean GetGameBean(String gameDetailString){

        GameBean game = gson.fromJson(gameDetailString, GameBean.class);
        return game;
    }

    // Get user Bean
    public UserLoginBean GetUserBean(String userDetailString){

        UserLoginBean user = gson.fromJson(userDetailString, UserLoginBean.class);
        return user;
    }

    // Get my list detail list Bean
    public MyListBean[] GetMyListBean(String myListDetailString){

        MyListBean[] myList = gson.fromJson(myListDetailString, MyListBean[].class);
        return myList;
    }

    // Get my list detail single Bean
    public MyListBean GetMyListSingleBean(String myListDetailString){

        MyListBean myList = gson.fromJson(myListDetailString, MyListBean.class);
        return myList;
    }

    // Get match list Bean
    public MatchBean[] GetMatchBean(String matchDetailedString){

        MatchBean[] match = gson.fromJson(matchDetailedString, MatchBean[].class);
        return match;
    }

    // Get address list Bean
    public AddressBean[] GetAddressListBean(String addressDetailedString){

        AddressBean[] addressList = gson.fromJson(addressDetailedString, AddressBean[].class);
        return addressList;
    }

    // Get address Bean
    public AddressBean GetAddressSingleBean(String addressDetailedString){

        AddressBean addressList = gson.fromJson(addressDetailedString, AddressBean.class);
        return addressList;
    }



    // Get my offer list detail list Bean
    public WishBean[] GetMyOfferListBean(String myListDetailString){

        WishBean[] myOfferList = gson.fromJson(myListDetailString, WishBean[].class);
        return myOfferList;
    }



    // Get user detail Bean
    public UserDetailBean GetUserDetailBean(String userDetailString){

        UserDetailBean userDetail = gson.fromJson(userDetailString, UserDetailBean.class);
        return userDetail;
    }


    // Get gameTile list Bean
    public List<GameTileBean> GetGameTileListBean(String gameTileDetail){
        GameTileBean[] gameArray = gson.fromJson(gameTileDetail, GameTileBean[].class);
        return new ArrayList<>(Arrays.asList(gameArray));
    }

    // Get gameDetail single Bean
    public GameDetailBean GetGameDetailSingleBean(String gameDetailJson){

        GameDetailBean gameDetail = gson.fromJson(gameDetailJson, GameDetailBean.class);
        return gameDetail;
    }
}
