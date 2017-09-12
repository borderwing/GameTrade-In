package com.example.ye.gametrade_in;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.ye.gametrade_in.fragment.SearchPaginationFragment;
import com.example.ye.gametrade_in.fragment.WishPaginationFragment;
import com.example.ye.gametrade_in.utils.SingleFragmentActivity;

/**
 * Created by lykav on 9/12/2017.
 */

public class WishListActivity extends SingleFragmentActivity {
    private static final String TAG = "SearchResultsActivity";

    private String mQuery;

    @Override
    protected Fragment createFragment() {
        return new WishPaginationFragment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
