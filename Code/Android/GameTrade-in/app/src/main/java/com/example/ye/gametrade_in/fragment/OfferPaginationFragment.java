package com.example.ye.gametrade_in.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;
import com.example.ye.gametrade_in.adapter.WishOfferPaginationAdapter;
import com.example.ye.gametrade_in.api.GameTradeService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/12/2017.
 */

public class OfferPaginationFragment extends PaginationFragment<WishBean> {


    public static OfferPaginationFragment newInstance() {

        Bundle args = new Bundle();
        args.putSerializable(WishOfferPaginationAdapter.ARG_TYPE,
                WishOfferPaginationAdapter.TYPE_OFFER);
        OfferPaginationFragment fragment = new OfferPaginationFragment();
        fragment.setArguments(args);
        return fragment;
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= super.onCreateView(inflater, container, savedInstanceState);
//        initOfferListView(v);
        return v;
    };

    @Override
    protected Call<List<WishBean>> callApi() {
        return mGameTradeService.getOfferList(
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
                return WishOfferPaginationAdapter.TYPE_OFFER;
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
