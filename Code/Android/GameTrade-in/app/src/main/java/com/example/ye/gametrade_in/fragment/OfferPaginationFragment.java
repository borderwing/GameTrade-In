package com.example.ye.gametrade_in.fragment;

import android.support.v4.app.Fragment;

import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;
import com.example.ye.gametrade_in.adapter.WishOfferPaginationAdapter;
import com.example.ye.gametrade_in.api.GameTradeService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/12/2017.
 */

public class OfferPaginationFragment extends PaginationFragment<WishBean> {
    @Override
    protected Call<List<WishBean>> callApi() {
        return mGameTradeService.getOfferList(
                mUserId,
                PAGE_SIZE,
                PAGE_SIZE * currentPage
        );
    }

    @Override
    protected LinearPaginationAdapter<WishBean> getNewAdapter(Fragment fragment) {
        return new WishOfferPaginationAdapter(fragment) {
            @Override
            public GameTradeService getGameTradeService() {
                return mGameTradeService;
            }
        };
    }
}
