package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lykav on 9/11/2017.
 */


public abstract class LinearPaginationAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<T> mItems;
    protected Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public LinearPaginationAdapter(Context context){
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;

        mItems = new ArrayList<>();
    }

    public LinearPaginationAdapter(Fragment fragment){
        this.context = fragment.getActivity();
        this.mCallback = (PaginationAdapterCallback) fragment;

        mItems = new ArrayList<>();
    }

    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List<T> items) {
        mItems = items;
    }

    protected abstract ItemHolder newItemHolder(View itemView);

    protected abstract T newItem();

    protected abstract class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public abstract void bind(T item);

    }

    protected abstract int getItemLayout();

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
                viewHolder = new LinearPaginationAdapter<T>.LoadingHolder(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(getItemLayout(), parent, false);
        viewHolder = newItemHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T item = mItems.get(position);

        switch(getItemViewType(position)){
            case ITEM:
                final LinearPaginationAdapter<T>.ItemHolder itemHolder =
                        (LinearPaginationAdapter<T>.ItemHolder) holder;
                itemHolder.bind(item);
                break;

            case LOADING:
                LinearPaginationAdapter<T>.LoadingHolder loadingHolder =
                        (LinearPaginationAdapter<T>.LoadingHolder) holder;

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
                    loadingHolder.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mItems.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }



    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(T item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void addAll(List<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    public void remove(T item) {
        int position = mItems.indexOf(item);
        if (position > -1) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear(){
        int oldSize = mItems.size();
        isLoadingAdded = false;
        mItems.clear();
        notifyDataSetChanged();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        T item = newItem();
        add(item);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mItems.size() - 1;
        T item = getItem(position);

        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mItems.size() - 1);

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
