package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
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
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.MatchedOfferBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.GameDetailActivity;
import com.example.ye.gametrade_in.MatchActivity;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.utils.GameDetailUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lykav on 9/12/2017.
 */

public abstract class MatchedOfferPaginationAdapter extends LinearPaginationAdapter<MatchedOfferBean> {

    private Fragment mFragment;

    public abstract GameTradeService getGameTradeService();

    public MatchedOfferPaginationAdapter(Context context) {
        super(context);
    }

    public MatchedOfferPaginationAdapter(Fragment fragment) {
        super(fragment);
        mFragment = fragment;
    }

    @Override
    protected ItemHolder newItemHolder(View itemView) {
        return new MatchedOfferPaginationAdapter.MatchedOfferHolder(itemView);
    }

    @Override
    protected MatchedOfferBean newItem() {
        return new MatchedOfferBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_wish_offer;
    }

    private class MatchedOfferHolder extends ItemHolder {
        private Long mIgdbId;

        private MatchedOfferBean mMatchedOffer;
        private GameDetailBean mGameDetail;

        private TextView mTitle;
        private ImageView mCover;

        private Button mMatchButton;

        private TextView mMeta;
        private TextView mWishUser;

        private ProgressBar mCoverProgress;
        private ProgressBar mWishProgress;

        private LinearLayout mWishContentLayout;

        private LinearLayout mErrorLayout;
        private Button mRetryButton;


        public MatchedOfferHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.item_wish_title);
            mCover = (ImageView) itemView.findViewById(R.id.item_wish_cover);


            mMeta = (TextView) itemView.findViewById(R.id.item_wish_meta);

            mCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_cover_progress);
            mWishProgress = (ProgressBar) itemView.findViewById(R.id.item_wish_progress);

            mWishUser = (TextView) itemView.findViewById(R.id.item_wish_user);

            mWishContentLayout = (LinearLayout) itemView.findViewById(R.id.item_wish_content);

            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            mRetryButton = (Button) itemView.findViewById(R.id.error_btn_retry);

            mMatchButton = (Button) itemView.findViewById(R.id.item_wish_match);

        }

        public void bind(MatchedOfferBean matchedOffer){
            mIgdbId = matchedOffer.getOfferGame().getGame().getIgdbId();
            mMatchedOffer = matchedOffer;

            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadContent();
                }
            });

            mMatchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO: ConfirmMatchJson needs gameId, targetUserId and addressId
                    // save these values in a bundle and start get address activity

                }
            });

            showLoading();
            loadContent();
        }

        private void bindContents(MatchedOfferBean matchedOffer, GameDetailBean gameDetail){
            mTitle.setText(gameDetail.getTitle());

            // TODO: move it to string.xml implementation
            mMatchButton.setText(String.valueOf(matchedOffer.getOfferPoint()) + " pts");

            GameDetailUtility utility = new GameDetailUtility(gameDetail);
            String platform = utility.getPlatformString(matchedOffer.getOfferGame().getGame().getPlatformId());
            String region = utility.getRegionString(matchedOffer.getOfferGame().getGame().getRegionId());

            mMeta.setText(platform + " | " + region);

            mWishUser.setText("from " + matchedOffer.getSender().getUsername());

            Glide
                    .with(context)
                    .load(gameDetail.getCoverUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            // TODO: 08/11/16 handle failure
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


//
//        @Override
//        public void onClick(View v) {
//            Log.d("Adapter", "clicked");
//
//            Intent intent = GameDetailActivity.newInent(context, mIgdbId);
//            context.startActivity(intent);
//        }

        private void loadContent(){
            callGameDetailApi(mIgdbId).enqueue(new Callback<GameDetailBean>() {
                @Override
                public void onResponse(Call<GameDetailBean> call, Response<GameDetailBean> response) {
                    // Got data. Send it to adapter
                    if(mFragment != null && !mFragment.isAdded())  return;

                    showContentLayout();
                    mGameDetail = response.body();
                    bindContents(mMatchedOffer, mGameDetail);

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

            mWishContentLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showErrorLayout(){
            mErrorLayout.setVisibility(View.VISIBLE);

            mWishContentLayout.setVisibility(View.GONE);
            mWishProgress.setVisibility(View.GONE);
        }

        private void showContentLayout(){
            mWishContentLayout.setVisibility(View.VISIBLE);

            mErrorLayout.setVisibility(View.GONE);
            mWishProgress.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private Call<GameDetailBean> callGameDetailApi(Long igdbId){
        return getGameTradeService().getDetailGame(igdbId);
    }



}
