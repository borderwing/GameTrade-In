package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.GameDetailActivity;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.activity.MatchWishActivity;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.fragment.OfferPaginationFragment;
import com.example.ye.gametrade_in.fragment.WishPaginationFragment;
import com.example.ye.gametrade_in.utils.GameDetailUtility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Path;

/**
 * Created by lykav on 9/12/2017.
 */

public abstract class WishOfferPaginationAdapter extends LinearPaginationAdapter<WishBean> {

    public static final String ARG_TYPE = "WishOfferPaginationAdapter.type";
    public static final int TYPE_OFFER = 1;
    public static final int TYPE_WISH = 2;


    public abstract int getAdapterType();

    private Fragment mFragment;
    private String operation;


    public abstract GameTradeService getGameTradeService();

    public WishOfferPaginationAdapter(Context context) {
        super(context);
    }

    public WishOfferPaginationAdapter(Fragment fragment) {
        super(fragment);
        mFragment = fragment;


        // operation = mFragment.getArguments().getString("operation");
    }

    @Override
    protected ItemHolder newItemHolder(View itemView) {
        return new WishOfferPaginationAdapter.WishOfferHolder(itemView);
    }

    @Override
    protected WishBean newItem() {
        return new WishBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_wish_offer;
    }

    private class WishOfferHolder extends ItemHolder {
        private Long mIgdbId;
        private WishBean mWishBean;
        private GameDetailBean mGameDetail;

        private TextView mTitle;
        private ImageView mCover;

        private TextView mCredit;
        private TextView mMeta;
        private ProgressBar mCoverProgress;
        private ProgressBar mWishProgress;

        private LinearLayout mWishContentLayout;

        private LinearLayout mErrorLayout;
        private Button mRetryButton;

        private Button mEditButton;
        private Button mMatchButton;


        public WishOfferHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.item_wish_title);
            mCover = (ImageView) itemView.findViewById(R.id.item_wish_cover);


            mCredit = (TextView) itemView.findViewById(R.id.item_wish_credit);
            mMeta = (TextView) itemView.findViewById(R.id.item_wish_meta);

            mCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_cover_progress);
            mWishProgress = (ProgressBar) itemView.findViewById(R.id.item_wish_progress);

            mWishContentLayout = (LinearLayout) itemView.findViewById(R.id.item_wish_content);

            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            mRetryButton = (Button) itemView.findViewById(R.id.error_btn_retry);

            mEditButton = (Button) itemView.findViewById(R.id.item_wish_edit);
            mMatchButton = (Button) itemView.findViewById(R.id.item_wish_match);

            switch (getAdapterType()){
                case TYPE_OFFER:
                    mMatchButton.setVisibility(View.GONE);
                    break;
                case TYPE_WISH:
                    break;
                default:
                    break;
            }
            /*if(operation == "offer"){
                mMatchButton.setVisibility(View.GONE);
            }*/

        }

        public void bind(WishBean wish){
            mIgdbId = wish.getGame().getIgdbId();
            mWishBean = wish;
            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadContent();
                }
            });

            showLoading();
            loadContent();
        }

        private void bindContents(final WishBean wish, final GameDetailBean gameDetail){
            mTitle.setText(gameDetail.getTitle());

            // TODO: move it to string.xml implementation
            mCredit.setText(String.valueOf(wish.getPoints()) + " pts");

            GameDetailUtility utility = new GameDetailUtility(gameDetail);
            String platform = utility.getPlatformString(wish.getGame().getPlatformId());
            String region = utility.getRegionString(wish.getGame().getRegionId());

            mMeta.setText(platform + " | " + region);

            if(getAdapterType() == TYPE_WISH){
                mMatchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = MatchWishActivity.newIntent(
                                mFragment.getContext(),
                                wish, gameDetail
                        );

                        mFragment.startActivity(intent);
                    }
                });
            }

            Glide
                    .with(context)
                    .load(gameDetail.getCoverUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if(mFragment != null && !mFragment.isAdded())  return false;
                            mCoverProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            if(mFragment != null && !mFragment.isAdded())  return false;
                            mCoverProgress.setVisibility(View.GONE);
                            return false;   // return false if you want Glide to handle everything else.
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                    .centerCrop()
                    .crossFade()
                    .into(mCover);

        }



        @Override
        public void onClick(View v) {
            Log.d("Adapter", "clicked");
            Intent intent = GameDetailActivity.newInent(context, mIgdbId);
            context.startActivity(intent);
        }

        private void loadContent(){
            callGameDetailApi(mIgdbId).enqueue(new Callback<GameDetailBean>() {
                @Override
                public void onResponse(Call<GameDetailBean> call, Response<GameDetailBean> response) {
                    // Got data. Send it to adapter
                    if(mFragment != null && !mFragment.isAdded())  return;

                    showContentLayout();
                    mGameDetail = response.body();

                    if(mGameDetail == null){
                        showErrorLayout();
                        return;
                    }

                    bindContents(mWishBean, mGameDetail);

                }

                @Override
                public void onFailure(Call<GameDetailBean> call, Throwable t) {
                    showErrorLayout();
                }
            });
        }

        private void showLoading(){
            mCoverProgress.setVisibility(View.VISIBLE);
            mWishProgress.setVisibility(View.VISIBLE);

            mWishContentLayout.setVisibility(View.INVISIBLE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showErrorLayout(){
            mErrorLayout.setVisibility(View.VISIBLE);

            mWishContentLayout.setVisibility(View.INVISIBLE);
            mWishProgress.setVisibility(View.GONE);
        }

        private void showContentLayout(){
            mWishContentLayout.setVisibility(View.VISIBLE);

            mErrorLayout.setVisibility(View.GONE);
            mWishProgress.setVisibility(View.GONE);
        }

    }

    private Call<GameDetailBean> callGameDetailApi(Long igdbId){
        return getGameTradeService().getDetailGame(igdbId);
    }

}
