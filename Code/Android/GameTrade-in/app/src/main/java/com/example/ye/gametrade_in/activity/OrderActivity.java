package com.example.ye.gametrade_in.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.ye.gametrade_in.MainActivity;
import com.example.ye.gametrade_in.fragment.OrderPaginationFragment;
import com.example.ye.gametrade_in.utils.SingleFragmentActivity;

/**
 * Created by lykav on 9/13/2017.
 */

public class OrderActivity extends SingleFragmentActivity {

    public static final String EXTRA_CALLER = "OrderActivity.caller";

    public static final int CALLER_ADDRESS = 1;
    public static final int CALLER_UNSPECIFIED = 0;
    public int caller = CALLER_UNSPECIFIED;

    private int mCaller;

    public static Intent newIntent(Context context, int caller){
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(EXTRA_CALLER, caller);
        return intent;
    }

    private void retrieveIntent(Intent intent){
        mCaller = intent.getIntExtra(EXTRA_CALLER, CALLER_UNSPECIFIED);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrieveIntent(getIntent());

    }

    @Override
    public void onBackPressed() {
        if(mCaller == CALLER_ADDRESS){
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected Fragment createFragment() {
        return new OrderPaginationFragment();
    }


}
