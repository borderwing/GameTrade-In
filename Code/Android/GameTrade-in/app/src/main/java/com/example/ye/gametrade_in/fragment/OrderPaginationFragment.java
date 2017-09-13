package com.example.ye.gametrade_in.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ye.gametrade_in.Bean.TradeOrderBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;
import com.example.ye.gametrade_in.adapter.OrderPaginationAdapter;
import com.example.ye.gametrade_in.adapter.WishOfferPaginationAdapter;
import com.example.ye.gametrade_in.api.GameTradeService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/12/2017.
 */

public class OrderPaginationFragment extends PaginationFragment<TradeOrderBean> {


    @Override
    protected Call<List<TradeOrderBean>> callApi() {
        return mGameTradeService.getOrderList(
                mUserId,
                PAGE_SIZE,
                PAGE_SIZE * currentPage
        );
    }

    @Override
    protected LinearPaginationAdapter<TradeOrderBean> getNewAdapter(Fragment fragment, Bundle bundle) {

        return new OrderPaginationAdapter(fragment) {

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
