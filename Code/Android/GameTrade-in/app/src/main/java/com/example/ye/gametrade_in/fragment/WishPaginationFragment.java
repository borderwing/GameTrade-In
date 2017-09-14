package com.example.ye.gametrade_in.fragment;

import android.os.Bundle;
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

public class WishPaginationFragment extends PaginationFragment<WishBean> {
    @Override
    protected Call<List<WishBean>> callApi() {
        return mGameTradeService.getWishList(
                mUserId,
                currentPage,
                PAGE_SIZE
        );
    }

    @Override
    protected LinearPaginationAdapter<WishBean> getNewAdapter(Fragment fragment, Bundle bundle) {
        return new WishOfferPaginationAdapter(fragment) {
            @Override
            public int getAdapterType() {
                return WishOfferPaginationAdapter.TYPE_WISH;
            }

            @Override
            public GameTradeService getGameTradeService() {
                return mGameTradeService;
            }

            @Override
            public Long getUserId() {
                return mUserId;
            }
        };
    }

}
