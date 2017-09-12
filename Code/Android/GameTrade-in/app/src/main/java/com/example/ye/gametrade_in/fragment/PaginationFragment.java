package com.example.ye.gametrade_in.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.QueryPreferences;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.adapter.GameTilePaginationAdapter;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;
import com.example.ye.gametrade_in.api.GameTradeApi;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.utils.PaginationAdapterCallback;
import com.example.ye.gametrade_in.utils.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lykav on 9/11/2017.
 */

public abstract class PaginationFragment<T> extends Fragment implements PaginationAdapterCallback {

    /*
    Fields for RecyclerView & error UI
 */
    protected LinearPaginationAdapter<T> mAdapter;
    protected LinearLayoutManager mLayoutManager;


    // Views for error layout
    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    // Views for no result layout
    LinearLayout noResultLayout;
    Button btnRetryNoResult;

    /*
        Fields for pagination & API fetching
     */

    protected static final int PAGE_START = 0;
    protected static final int TOTAL_PAGES = 5;
    protected static final int PAGE_SIZE = 10;

    protected boolean isLoading = false;
    protected boolean isLastPage = false;

    protected int currentPage = PAGE_START;

    GameTradeService mGameTradeService;

    protected int mUserId;

    protected abstract Call<List<T>> callApi() ;

    protected abstract LinearPaginationAdapter<T> getNewAdapter(Fragment fragment);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String authorizedHeader =
                    QueryPreferences.getStoredAuthorizedQuery(this.getActivity().getApplicationContext());

        if(QueryPreferences.getStoredUserIdQuery(this.getActivity().getApplicationContext()) != null){
            mUserId = Integer.parseInt(
                        QueryPreferences.getStoredUserIdQuery(this.getActivity().getApplicationContext())
                );
        }


        if (authorizedHeader == null) {
                mGameTradeService = GameTradeApi.getClient().create(GameTradeService.class);
            } else {
                mGameTradeService = GameTradeApi
                        .getClient(authorizedHeader)
                        .create(GameTradeService.class);
            }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_linear_page, container, false);

        /*
            setup recyclerView & pagination
        */
        rv = (RecyclerView) v.findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) v.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
        btnRetry = (Button) v.findViewById(R.id.error_btn_retry);
        txtError = (TextView) v.findViewById(R.id.error_txt_cause);

        noResultLayout = (LinearLayout) v.findViewById(R.id.no_result);
        btnRetryNoResult = (Button) v.findViewById(R.id.no_result_btn_retry);


        mAdapter = getNewAdapter(this);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(mAdapter);

        rv.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
        btnRetryNoResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return v;
    }

    protected void loadFirstPage(){
        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();
        hideNoResultView();

        callApi().enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                // Got data. Send it to adapter
                if(!isAdded())  return;
                hideErrorView();

                List<T> results = response.body();
                progressBar.setVisibility(View.GONE);

                if(results.size() > 0) {
                    mAdapter.addAll(results);

                    if(results.size() < PAGE_SIZE){
                        isLastPage = true;
                        return;
                    }

                    if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                    else isLastPage = true;
                } else{
                    currentPage --;
                    showNoResultView();
                }
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                if(!isAdded())  return;
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    protected void loadNextPage(){
//        Log.d(TAG, "loadNextPage: " + currentPage);

        callApi().enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if(!isAdded())  return;

                mAdapter.removeLoadingFooter();
                isLoading = false;

                List<T> results = response.body();
                mAdapter.addAll(results);

                if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                if(!isAdded())  return;

                t.printStackTrace();
                mAdapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }


    public void retryPageLoad() {
        loadNextPage();
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }



    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showNoResultView(){
        if (noResultLayout.getVisibility() == View.GONE) {
            noResultLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideNoResultView(){
        if(noResultLayout.getVisibility() == View.VISIBLE){
            noResultLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
