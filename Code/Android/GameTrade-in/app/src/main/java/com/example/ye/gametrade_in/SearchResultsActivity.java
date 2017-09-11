package com.example.ye.gametrade_in;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ye.gametrade_in.fragment.SearchPaginationFragment;
import com.example.ye.gametrade_in.utils.SingleFragmentActivity;

/**
 * Created by lykav on 9/11/2017.
 */

public class SearchResultsActivity extends SingleFragmentActivity {
    private static final String TAG = "SearchResultsActivity";

    private String mQuery;

    @Override
    protected Fragment createFragment() {
        return SearchPaginationFragment.newInstance(mQuery);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction())){
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Query: " + query);
            mQuery = query;
        }
        super.onCreate(savedInstanceState);
    }
}
