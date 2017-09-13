package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.TradeConfirmBean;
import com.example.ye.gametrade_in.Bean.TradeOrderBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.GameDetailActivity;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.utils.GameDetailUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lykav on 9/12/2017.
 */

public abstract class OrderPaginationAdapter extends LinearPaginationAdapter<TradeOrderBean> {

//    public static final String ARG_TYPE = "WishOfferPaginationAdapter.type";
//    public static final int TYPE_OFFER = 1;
//    public static final int TYPE_WISH = 2;

    private Fragment mFragment;

    public abstract GameTradeService getGameTradeService();
    public abstract Long getUserId();

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
    protected TradeOrderBean newItem() {
        return new TradeOrderBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_order;
    }

    private class OrderHolder extends ItemHolder {

        private Long mOrderId;

        private GameDetailBean mWishDetail;
        private GameDetailBean mOfferDetail;

        private TradeOrderBean mTradeOrder;

        private LinearLayout mOrderLayout;

        private TextView mOrderStatus;


        private TextView mWishExplain;
        private TextView mWishTitle;
        private ImageView mWishCover;
        private TextView mWishPoints;
        private TextView mWishMeta;
        private ProgressBar mWishCoverProgress;
        private TextView mWishAddress;

        private TextView mOfferExplain;
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

            mOrderLayout = (LinearLayout) itemView.findViewById(R.id.item_order_layout);

            mWishExplain = (TextView) itemView.findViewById(R.id.item_order_wish_explain);
            mWishTitle = (TextView) itemView.findViewById(R.id.item_order_wish_title);
            mWishCover = (ImageView) itemView.findViewById(R.id.item_order_wish_cover);
            mWishPoints = (TextView) itemView.findViewById(R.id.item_order_wish_points);
            mWishMeta = (TextView) itemView.findViewById(R.id.item_order_wish_meta);
            mWishCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_order_wish_cover_progress);
            mWishAddress = (TextView) itemView.findViewById(R.id.item_order_wish_address);


            mOfferExplain = (TextView) itemView.findViewById(R.id.item_order_offer_explain);
            mOfferTitle = (TextView) itemView.findViewById(R.id.item_order_offer_title);
            mOfferCover = (ImageView) itemView.findViewById(R.id.item_order_offer_cover);
            mOfferPoints = (TextView) itemView.findViewById(R.id.item_order_offer_points);
            mOfferMeta = (TextView) itemView.findViewById(R.id.item_order_offer_meta);
            mOfferCoverProgress = (ProgressBar) itemView.findViewById(R.id.item_order_offer_cover_progress);
            mOfferAddress = (TextView) itemView.findViewById(R.id.item_order_offer_address);

            mOrderProgress = (ProgressBar) itemView.findViewById(R.id.item_order_progress);
            mOrderStatus = (TextView) itemView.findViewById(R.id.item_order_status);


            mOrderWishContentLayout = (LinearLayout) itemView.findViewById(R.id.item_order_wish_content);
            mOrderOfferContentLayout = (LinearLayout) itemView.findViewById(R.id.item_order_offer_content);

            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.error_layout);
            mRetryButton = (Button) itemView.findViewById(R.id.error_btn_retry);

            mOrderConfirmButton = (Button) itemView.findViewById(R.id.item_order_button_confirm);
            mOrderCancelButton = (Button) itemView.findViewById(R.id.item_order_button_cancel);



        }

        @Override
        public void bind(TradeOrderBean tradeOrder) {
            mOrderId = tradeOrder.getOrderId();
            mTradeOrder = tradeOrder;

            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTradeOrder != null) {
                        bind(mTradeOrder);
                    }
                }
            });


            showLoading();
            mOrderLayout.setVisibility(View.INVISIBLE);

            callGameDetailApi(tradeOrder.getWishGame().getIgdbId())
                    .enqueue(new Callback<GameDetailBean>() {
                        @Override
                        public void onResponse(Call<GameDetailBean> call, Response<GameDetailBean> response) {
                            mWishDetail = response.body();
                            if(mWishDetail != null){
                                bindWishGameDetail(mTradeOrder.getWishGame(), mWishDetail);
                                if(mOfferDetail != null){
                                    showContentLayout();
                                }
                            } else {
                                showErrorLayout();
                            }
                        }

                        @Override
                        public void onFailure(Call<GameDetailBean> call, Throwable t) {
                            showErrorLayout();
                        }
                    });

            callGameDetailApi(tradeOrder.getOfferGame().getIgdbId())
                    .enqueue(new Callback<GameDetailBean>() {
                        @Override
                        public void onResponse(Call<GameDetailBean> call, Response<GameDetailBean> response) {
                            mOfferDetail = response.body();
                            if(mOfferDetail != null){
                                bindOfferGameDetail(mTradeOrder.getOfferGame(), mOfferDetail);
                                if(mWishDetail != null){
                                    showContentLayout();
                                }
                            } else {
                                showErrorLayout();
                            }
                        }

                        @Override
                        public void onFailure(Call<GameDetailBean> call, Throwable t) {
                            showErrorLayout();
                        }
                    });

            // set order status text view
            switch(tradeOrder.getStatus()) {
                case -1:
                    mOrderStatus.setText(R.string.trade_order_status_cancelled);
                    mOrderConfirmButton.setVisibility(View.INVISIBLE);
                    mOrderCancelButton.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    mOrderStatus.setText(R.string.trade_order_status_completed);
                    mOrderConfirmButton.setVisibility(View.INVISIBLE);
                    mOrderCancelButton.setVisibility(View.INVISIBLE);
                    break;
                default:
                    if(tradeOrder.getYouAddress() == null) {
                        mOrderStatus.setText(R.string.trade_order_status_wait_you);

                    } else {
                        mOrderStatus.setText(R.string.trade_order_status_wait_other);
                    }
            }

            mOrderCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderConfirmButton.setEnabled(false);
                    mOrderCancelButton.setEnabled(false);


                }
            });

            mOrderCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderConfirmButton.setEnabled(false);
                    mOrderCancelButton.setEnabled(false);
                    callRejectOrderApi(getUserId(), mOrderId).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.code() == 200){
                                mOrderCancelButton.setText("Cancelled");
                            } else {
                                Toast.makeText(mFragment.getContext(),
                                        "Http Status " + response.code(),
                                        Toast.LENGTH_SHORT);
                                mOrderConfirmButton.setEnabled(true);
                                mOrderCancelButton.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(mFragment.getContext(),
                                    t.toString(),
                                    Toast.LENGTH_SHORT);
                            mOrderConfirmButton.setEnabled(true);
                            mOrderCancelButton.setEnabled(true);
                        }
                    });
                }
            });


            String otherName = tradeOrder.getTargetAddress().getReceiver();

            // set explain text
            mWishExplain.setText("Receive this game from: " + otherName);
            mOfferExplain.setText("Send this game to: " + otherName);


            AddressBean yourAddress = mTradeOrder.getYouAddress();
            AddressBean targetAddress = mTradeOrder.getTargetAddress();

            if(yourAddress != null) {
                List<String> yourList = Arrays.asList(
                        yourAddress.getReceiver(), yourAddress.getPhone(),
                        yourAddress.getRegion(), yourAddress.getAddress()
                );
                mWishAddress.setText(TextUtils.join("\n", yourList));
            }

            if(targetAddress != null) {
                List<String> targetList = Arrays.asList(
                        targetAddress.getReceiver(), targetAddress.getPhone(),
                        targetAddress.getRegion(), targetAddress.getAddress()
                );
                mOfferAddress.setText(TextUtils.join("\n", targetList));

            }

            // set points
            mWishPoints.setText(mTradeOrder.getWishPoints() + " pts");
            mOfferPoints.setText(mTradeOrder.getOfferPoints() + " pts");


        }

        @Override
        public void onClick(View v) {

        }


        public void bindWishGameDetail(GameBean wish, GameDetailBean gameDetail) {

            mWishTitle.setText(gameDetail.getTitle());
            GameDetailUtility utility = new GameDetailUtility(gameDetail);
            String platform = utility.getPlatformString(wish.getPlatformId());
            String region = utility.getRegionString(wish.getRegionId());

            mWishMeta.setText(platform + " | " + region);

            Glide
                    .with(context)
                    .load(gameDetail.getCoverUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mWishCoverProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            mWishCoverProgress.setVisibility(View.GONE);
                            return false;   // return false if you want Glide to handle everything else.
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                    .centerCrop()
                    .crossFade()
                    .into(mWishCover);
        }


        public void bindOfferGameDetail(GameBean offer, GameDetailBean gameDetail) {
            mOfferTitle.setText(gameDetail.getTitle());
            GameDetailUtility utility = new GameDetailUtility(gameDetail);
            String platform = utility.getPlatformString(offer.getPlatformId());
            String region = utility.getRegionString(offer.getRegionId());

            mOfferMeta.setText(platform + " | " + region);

            Glide
                    .with(context)
                    .load(gameDetail.getCoverUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mOfferCoverProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            mOfferCoverProgress.setVisibility(View.GONE);
                            return false;   // return false if you want Glide to handle everything else.
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                    .centerCrop()
                    .crossFade()
                    .into(mOfferCover);

        }

        private void showLoading(){
            mWishCoverProgress.setVisibility(View.VISIBLE);
            mOfferCoverProgress.setVisibility(View.VISIBLE);
            mOrderProgress.setVisibility(View.VISIBLE);

            mOrderWishContentLayout.setVisibility(View.INVISIBLE);
            mOrderOfferContentLayout.setVisibility(View.INVISIBLE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showErrorLayout(){
            mErrorLayout.setVisibility(View.VISIBLE);

            mOrderWishContentLayout.setVisibility(View.INVISIBLE);
            mOrderOfferContentLayout.setVisibility(View.INVISIBLE);
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

    private Call<String> callRejectOrderApi(Long userId, Long orderId){
        return getGameTradeService().rejectOrder(userId, orderId);
    }
}











