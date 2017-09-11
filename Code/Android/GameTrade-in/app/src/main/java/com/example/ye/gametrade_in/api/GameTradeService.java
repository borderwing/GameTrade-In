package com.example.ye.gametrade_in.api;

import com.example.ye.gametrade_in.Bean.GameTileBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
