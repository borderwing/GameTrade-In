package com.example.ye.gametrade_in;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lykav on 9/10/2017.
 */

public class TilePaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<GameTileBean> mGameTiles;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public TilePaginationAdapter(Context context){
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        mGameTiles = new ArrayList<>();
    }

    public List<GameTileBean> getGameTiles() {
        return mGameTiles;
    }

    public void setGameTiles(List<GameTileBean> gameTiles) {
        mGameTiles = gameTiles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingHolder(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_game, parent, false);
        viewHolder = new GameTileHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GameTileBean gameTile = mGameTiles.get(position);

        switch(getItemViewType(position)){
            case ITEM:
                final GameTileHolder gameTileHolder = (GameTileHolder) holder;
                gameTileHolder.bindGameTile(gameTile);
                break;

            case LOADING:
                LoadingHolder loadingHolder = (LoadingHolder) holder;
                if(retryPageLoad){
                    loadingHolder.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingHolder.mProgressBar.setVisibility(View.GONE);
                    loadingHolder.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    "An unexpected error occurred"
                    );
                } else {
                    loadingHolder.mErrorLayout.setVisibility(View.GONE);
                    loadingHolder.mProgressBar.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mGameTiles == null ? 0 : mGameTiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mGameTiles.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }



    protected class GameTileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public void bindGameTile(GameTileBean gameTile){
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

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(GameTileBean gameTile) {
        mGameTiles.add(gameTile);
        notifyItemInserted(mGameTiles.size() - 1);
    }

    public void addAll(List<GameTileBean> gameTiles) {
        for (GameTileBean gameTile : gameTiles) {
            add(gameTile);
        }
    }

    public void remove(GameTileBean gameTile) {
        int position = mGameTiles.indexOf(gameTile);
        if (position > -1) {
            mGameTiles.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear(){
        int oldSize = mGameTiles.size();
        isLoadingAdded = false;
        mGameTiles.clear();
        notifyItemRangeRemoved(0, oldSize - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new GameTileBean());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mGameTiles.size() - 1;
        GameTileBean gameTile = getItem(position);

        if (gameTile != null) {
            mGameTiles.remove(position);
            notifyItemRemoved(position);
        }
    }

    public GameTileBean getItem(int position) {
        return mGameTiles.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mGameTiles.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    protected class LoadingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingHolder(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }


}
