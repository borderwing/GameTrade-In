package com.example.ye.gametrade_in.api;

import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.GameTransportBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lykav on 9/10/2017.
 */

public interface GameTradeService {
    @GET("game/trending")
    Call<List<GameTileBean>> getTrendingGames(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("game/search")
    Call<List<GameTileBean>> getSearchedGames(
            @Query("keyword") String keyword,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("game/{igdbId}")
    Call<GameDetailBean> getDetailGame(
            @Path("igdbId") Long igdbId
    );

    @POST("user/{userId}/wishlist")
    Call<POST> saveWishItem(
            @Path("userId") Long userId,
            @Body GameTransportBean gameTransport
    );



}
