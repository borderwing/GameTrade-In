package com.example.ye.gametrade_in.api;

import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.GameTransportBean;
import com.example.ye.gametrade_in.Bean.MatchedOfferBean;
import com.example.ye.gametrade_in.Bean.TradeConfirmBean;
import com.example.ye.gametrade_in.Bean.TradeOrderBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.Bean.temp.ModifyWishOfferBean;
import com.example.ye.gametrade_in.Bean.temp.CreateOrderBean;
import com.example.ye.gametrade_in.Bean.temp.OrderConfirmBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lykav on 9/10/2017.
 */

public interface GameTradeService {
    @GET("game/trending")
    Call<List<GameTileBean>> getTrendingGames(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("game/search")
    Call<List<GameTileBean>> getSearchedGames(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("game/{igdbId}")
    Call<GameDetailBean> getDetailGame(
            @Path("igdbId") Long igdbId
    );

    @GET("user/{userId}/address/params")
    Call<List<AddressBean>> getAddressList(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("user/{userId}/wishlist")
    Call<String> saveWishItem(
            @Path("userId") Long userId,
            @Body GameTransportBean gameTransport
    );

    @POST("user/{userId}/offerlist ")
    Call<String> saveOfferItem(
            @Path("userId") Long userId,
            @Body GameTransportBean gameTransport
    );

    @GET("user/{userId}/wishlist/params")
    Call<List<WishBean>> getWishList(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );


    @GET("user/{userId}/offerlist/params")
    Call<List<WishBean>> getOfferList(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("user/{userId}/wishlist/{gameId}/match")
    Call<List<MatchedOfferBean>> getMatchedOffers(
            @Path("userId") Long userId,
            @Path("gameId") Long gameId,
            @Query("scale") int scale
    );

    @PUT("user/{userId}/wishlist/{gameId}/modify")
    Call<String> modifyWishItem(
            @Path("userId") Long userId,
            @Path("gameId") Long gameId,
            @Body ModifyWishOfferBean bean
    );

    @POST("user/{userId}/wishlist/{myGameId}/match/confirm")
    Call<TradeConfirmBean> confirmMatch(
            @Path("userId") Long userId,
            @Path("myGameId") Long myGameId,
            @Body CreateOrderBean createOrder
    );

    @GET("user/{userId}/order/params")
    Call<List<TradeOrderBean>> getOrderList(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("user/{userId}/order/{orderId}/confirm")
    Call<String> confirmOrder(
            @Path("userId") Long userId,
            @Path("orderId") Long orderId,
            @Body OrderConfirmBean orderConfirm
    );

    @PUT("user/{userId}/order/{orderId}/refuse")
    Call<String> rejectOrder(
            @Path("userId") Long userId,
            @Path("orderId") Long orderId
    );

    @PUT("user/{userId}/wishlist/{gameId}/delete")
    Call<String> deleteWish(
            @Path("userId") Long userId,
            @Path("gameId") Long gameId
    );

    @PUT("user/{userId}/offerlist/{gameId}/delete")
    Call<String> deleteOffer(
            @Path("userId") Long userId,
            @Path("gameId") Long gameId
    );
}
