package com.example.ye.gametrade_in.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.adapter.GameTilePaginationAdapter;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/11/2017.
 */

public class GameTilePaginationFragment extends PaginationFragment<GameTileBean> {

    @Override
    protected Call<List<GameTileBean>> callApi() {
        return mGameTradeService.getTrendingGames(
                PAGE_SIZE,
                PAGE_SIZE * currentPage
        );
    }

    @Override
    protected LinearPaginationAdapter<GameTileBean> getNewAdapter(Fragment fragment) {
        return new GameTilePaginationAdapter(fragment);
    }
}