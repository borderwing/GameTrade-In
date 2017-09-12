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
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.GameDetailActivity;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.utils.GameDetailUtility;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lykav on 9/12/2017.
 */

public abstract class OrderPaginationAdapter extends LinearPaginationAdapter<WishBean> {

    /*public static final String ARG_TYPE = "WishOfferPaginationAdapter.type";
    public static final int TYPE_OFFER = 1;
    public static final int TYPE_WISH = 2;*/


    /*public abstract int getAdapterType();*/

    private Fragment mFragment;



    public abstract GameTradeService getGameTradeService();

    public OrderPaginationAdapter(Context context) {
        super(context);
    }

    public OrderPaginationAdapter(Fragment fragment) {
        super(fragment);
        mFragment = fragment;
    }

    @Override
    protected ItemHolder newItemHolder(View itemView) {
        return new OrderPaginationAdapter.OrderHolder(itemView);
    }

    @Override
    protected WishBean newItem() {
        return new WishBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_order;
    }

    private class OrderHolder extends ItemHolder {

        private Long mIgdbId;
        private WishBean mWishBean;
        private GameDetailBean mGameDetail;

        private TextView mOrderStatus;

        private TextView mWishTitle;
        private ImageView mWishCover;
        private TextView mWishPoints;
        private TextView mWishMeta;
        private ProgressBar mWishCoverProgress;
        private TextView mWishAddress;

        private TextView mOfferTitle;
        private ImageView mOfferCover;
        private TextView mOfferPoints;
        private TextView mOfferMeta;
        private ProgressBar mOfferCoverProgress;
        private TextView mOfferAddress;

        private ProgressBar mOrderProgress;

        private LinearLayout mOrderWishContentLayout;
        private LinearLayout mOrderOfferContentLayout;

        private LinearLayout mErrorLayout;
        private Button mRetryButton;

        private Button mOrderConfirmButton;
        private Button mOrderCancelButton;


        public OrderHolder(View itemView) {
            super(itemView);

            mWishTitle = (TextView) itemView.findViewById(R.id.item_order_wish_title);
            mWishCover = (ImageView) itemView.findViewById(R.id.item_order_wish_cover);
            mWishPoints = (TextView) itemView.findViewById(R.id.item_order_wish_points);
            mWishMeta = (TextView) itemView.findViewById(R.id.item_order_wish_meta);
            mWishCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_order_wish_cover_progress);
            mWishAddress = (TextView) itemView.findViewById(R.id.item_order_wish_address);

            mOfferTitle = (TextView) itemView.findViewById(R.id.item_order_offer_title);
            mOfferCover = (ImageView) itemView.findViewById(R.id.item_order_offer_cover);
            mOfferPoints = (TextView) itemView.findViewById(R.id.item_order_offer_points);
            mOfferMeta = (TextView) itemView.findViewById(R.id.item_order_offer_meta);
            mOfferCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_order_offer_cover_progress);
            mOfferAddress = (TextView) itemView.findViewById(R.id.item_order_offer_address);

            mOrderProgress = (ProgressBar) itemView.findViewById(R.id.item_order_progress);
            mOrderStatus = (TextView) itemView.findViewById(R.id.item_order_status);


            mOrderWishContentLayout = (LinearLayout) itemView.findViewById(R.id.item_order_wish_content);
            mOrderOfferContentLayout = (LinearLayout) itemView.findViewById(R.id.item_order_offer_content)

            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            mRetryButton = (Button) itemView.findViewById(R.id.error_btn_retry);

            mOrderConfirmButton = (Button) itemView.findViewById(R.id.item_order_button_confirm);
            mOrderCancelButton = (Button) itemView.findViewById(R.id.item_order_button_cancel);

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


        // TODO: NEWBINDCONTENT INCLUDING ORDERBEAN
        private void bindContents(WishBean wish, GameDetailBean gameDetail){
            mWishTitle.setText(orderDetail.getwishTitle());
            mOfferTitle.setText(orderDetail.getofferTitle());




            // TODO: move it to string.xml implementation
            // mCredit.setText(String.valueOf(wish.getPoints()) + " pts");
            mWishPoints.setText(String.valueOf(order.getWishPoints()+"pts"));
            mOfferPoints.setText(String.valueOf(order.getOrderPoints()+"pts"));










            GameDetailUtility utility = new GameDetailUtility(gameDetail);
            String platform = utility.getPlatformString(wish.getGame().getPlatformId());
            String region = utility.getRegionString(wish.getGame().getRegionId());

            mMeta.setText(platform + " | " + region);

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
                    bindContents(mWishBean, mGameDetail);

                }

                @Override
                public void onFailure(Call<GameDetailBean> call, Throwable t) {
                    showErrorLayout();
                }
            });
        }





        private void showLoading(){
            mWishCoverProgress.setVisibility(View.VISIBLE);
            mOfferCoverProgress.setVisibility(View.VISIBLE);
            mOrderProgress.setVisibility(View.VISIBLE);

            mOrderWishContentLayout.setVisibility(View.GONE);
            mOrderOfferContentLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showErrorLayout(){
            mErrorLayout.setVisibility(View.VISIBLE);

            mOrderWishContentLayout.setVisibility(View.GONE);
            mOrderOfferContentLayout.setVisibility(View.GONE);
            mOrderProgress.setVisibility(View.GONE);
        }

        private void showContentLayout(){
            mOrderWishContentLayout.setVisibility(View.VISIBLE);
            mOrderOfferContentLayout.setVisibility(View.VISIBLE);

            mErrorLayout.setVisibility(View.GONE);
            mOrderProgress.setVisibility(View.GONE);
        }

    }

    private Call<GameDetailBean> callGameDetailApi(Long igdbId){
        return getGameTradeService().getDetailGame(igdbId);
    }

}
