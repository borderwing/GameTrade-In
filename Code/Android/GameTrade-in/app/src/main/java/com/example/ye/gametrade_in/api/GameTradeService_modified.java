package com.example.ye.gametrade_in.api;

import com.example.ye.gametrade_in.Bean.AddressPostBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.GameTransportBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lykav on 9/10/2017.
 */

public interface GameTradeService_modified {
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


    @POST("user/{userid}/wishlist")
    Call<GameTransportBean> addWishGames(
            /*@Field("igdbId") int igdbId,
            @Field("platformId") int platformId,
            @Field("regionId") int regionId,
            @Field("points") int points*/
            @Path("userid") int userId,
            @Body GameTransportBean gameTransportBean
    );

    @POST("user/{userid}/offerlist")
    Call<GameTransportBean> addOfferGames(
            /*@Field("igdbId") int igdbId,
            @Field("platformId") int platformId,
            @Field("regionId") int regionId,
            @Field("points") int points*/
            @Path("userid") int userId,
            @Body GameTransportBean gameTransportBean
    );

    @PUT("user/{userid}/wishlist/{gameid}/modify")
    Call<Integer> modifyWishGame(
            @Path("userid") int userId,
            @Path("gameid") int gameId,
            @Field("points") int points
    );

    @PUT("user/{userid}/offerlist/{gameid}/modify")
    Call<Integer> modifyOfferGame(
            @Path("userid") int userId,
            @Path("gameid") int gameId,
            @Field("points") int points
    );

    @POST("user/{userid}/address")
    Call<AddressPostBean> addAddress(
            @Path("userid") int userId,
            @Body AddressPostBean addressPostBean
    );

    @PUT("user/{userid}/address/{addressid}")
    Call<AddressPostBean> modifyAddress(
            @Path("userid") int userId,
            @Path("addressid") int addressId,
            @Body AddressPostBean addressPostBean
    );

    @PUT("user/{userid}/order/{orderid}/confirm")
    Call<Integer> confirmOrder(
            @Path("userid") int userId,
            @Path("orderid") int orderId,
            @Field("gameId") int gameId,
            @Field("targetUserId") int targetUserId,
            @Field("addressId") int addressId
    );


    @POST("login")
    Call<String> login(
            @Field("username") String username,
            @Field("password") String password
    );




}
