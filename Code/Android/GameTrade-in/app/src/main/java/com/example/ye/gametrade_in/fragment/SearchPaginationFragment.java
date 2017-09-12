package com.example.ye.gametrade_in.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.adapter.GameTilePaginationAdapter;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/11/2017.
 */

public class SearchPaginationFragment extends PaginationFragment<GameTileBean> {

    private static final String ARG_QUERY = "Query";

    private String mQuery;

    public static SearchPaginationFragment newInstance(String query){
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);

        SearchPaginationFragment fragment = new SearchPaginationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mQuery = getArguments().getString(ARG_QUERY);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Call<List<GameTileBean>> callApi() {
        return mGameTradeService.getSearchedGames(
                mQuery,
                PAGE_SIZE,
                PAGE_SIZE * currentPage
        );
    }

    @Override
    protected LinearPaginationAdapter<GameTileBean> getNewAdapter(Fragment fragment, Bundle bundle) {
        return new GameTilePaginationAdapter(fragment);
    }
}
