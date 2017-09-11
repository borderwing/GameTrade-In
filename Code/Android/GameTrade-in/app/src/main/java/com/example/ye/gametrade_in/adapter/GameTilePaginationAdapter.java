package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.R;

/**
 * Created by lykav on 9/11/2017.
 */

public class GameTilePaginationAdapter extends LinearPaginationAdapter<GameTileBean> {


    public GameTilePaginationAdapter(Context context) {
        super(context);
    }

    public GameTilePaginationAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    protected ItemHolder newItemHolder(View itemView) {
        return new GameTileHolder(itemView);
    }

    @Override
    protected GameTileBean newItem() {
        return new GameTileBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_game;
    }

    private class GameTileHolder extends ItemHolder{
        private Long mIgdbId;

        private TextView mTitle;
        private TextView mSummary;
        private TextView mPlatform;
        private ImageView mCover;
        private ProgressBar mProgress;


        public GameTileHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.item_game_title);
            mSummary = (TextView) itemView.findViewById(R.id.item_game_summary);
            mPlatform = (TextView) itemView.findViewById(R.id.item_game_platforms);
            mCover = (ImageView) itemView.findViewById(R.id.item_game_cover);
            mProgress = (ProgressBar) itemView.findViewById(R.id.item_game_progress);
        }

        public void bind(GameTileBean gameTile){
            mIgdbId = gameTile.getIgdbId();

            mTitle.setText(gameTile.getTitle());
            mSummary.setText(gameTile.getSummary());
            // set platform to e.g.: "PS4, Xbox"
            mPlatform.setText(TextUtils.join(", ", gameTile.getPlatforms()));


            Glide
                    .with(context)
                    .load(gameTile.getCoverUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            // TODO: 08/11/16 handle failure
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            mProgress.setVisibility(View.GONE);
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
            // TODO: implement onClick behaviour for game tiles
        }
    }
}
