package com.example.ye.gametrade_in.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.MatchedOfferBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;
import com.example.ye.gametrade_in.adapter.MatchedOfferPaginationAdapter;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;

/**
 * Created by lykav on 9/12/2017.
 */

public class MatchedOfferReloadableFragment extends ReloadableFragment<MatchedOfferBean> {


    private static final String ARG_GAME_ID = "MatchedOfferReloadableFragment.game_id";
    private static final String ARG_PLATFORM_ID = "MatchedOfferReloadableFragment.platform_id";
    private static final String ARG_REGION_ID = "MatchedOfferReloadableFragment.region_id";


    private static final int SCALE = 500;

    private Long mGameId;

//    private WishBean mWish;
//    private GameDetailBean mGameDetail;


    public static MatchedOfferReloadableFragment newInstance
            (long gameId) {
        Bundle args = new Bundle();

        args.putLong(ARG_GAME_ID, gameId);

        MatchedOfferReloadableFragment fragment = new MatchedOfferReloadableFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void retrieveArguments(Bundle args){
        mGameId = getArguments().getLong(ARG_GAME_ID);
//        Gson gson = new Gson();
//        mWish = gson.fromJson(args.getString(ARG_WISH), WishBean.class);
//        mGameDetail = gson.fromJson(args.getString(ARG_GAME_DEATIL), GameDetailBean.class);
//        mGameId = mWish.getGame().getGameId();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveArguments(getArguments());


    }

    @Override
    protected Call<List<MatchedOfferBean>> callApi() {
        return mGameTradeService.getMatchedOffers(
                    mUserId, mGameId, SCALE
                );
    }

    @Override
    protected LinearPaginationAdapter<MatchedOfferBean> getNewAdapter(Fragment fragment) {
        return new MatchedOfferPaginationAdapter(fragment) {
            @Override
            public GameTradeService getGameTradeService() {
                return mGameTradeService;
            }
        };
    }
}
