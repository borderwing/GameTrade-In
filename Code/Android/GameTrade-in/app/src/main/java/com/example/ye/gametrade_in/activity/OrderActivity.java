package com.example.ye.gametrade_in.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.ye.gametrade_in.fragment.OrderPaginationFragment;
import com.example.ye.gametrade_in.utils.SingleFragmentActivity;

/**
 * Created by lykav on 9/13/2017.
 */

public class OrderActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new OrderPaginationFragment();
    }
}
