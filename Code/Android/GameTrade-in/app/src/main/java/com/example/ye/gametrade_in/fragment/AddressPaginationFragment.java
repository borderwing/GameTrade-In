package com.example.ye.gametrade_in.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.ye.gametrade_in.AddressActivity;
import com.example.ye.gametrade_in.AddressOperationActivity;
import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.TradeConfirmBean;
import com.example.ye.gametrade_in.Bean.temp.CreateOrderBean;
import com.example.ye.gametrade_in.Bean.temp.OrderConfirmBean;
import com.example.ye.gametrade_in.activity.OrderActivity;
import com.example.ye.gametrade_in.adapter.AddressPaginationAdapter;
import com.example.ye.gametrade_in.adapter.GameTilePaginationAdapter;
import com.example.ye.gametrade_in.adapter.LinearPaginationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lykav on 9/13/2017.
 */

public class AddressPaginationFragment extends PaginationFragment<AddressBean> {

    private static final int OPERATION_MATCH = 1;
    private static final int OPERATION_CONFIRM = 2;
    private static final int OPERATION_BROWSE = 0;

    private int OPERATION = OPERATION_BROWSE;

    private static final String ARG_MY_GAME_ID = "EXTRA_MY_GAME_ID";
    private static final String ARG_TARGET_GAME_ID =  "EXTRA_TARGET_GAME_ID";
    private static final String ARG_TARGET_USER_ID =  "EXTRA_TARGET_USER_ID";
    private static final String ARG_ORDER_ID = "EXTRA_ORDER_ID";

    private static final String ARG_OPERATION = "EXTRA_OPERATION";

    Long myGameId;
    Long targetGameId;
    Long targetUserId;
    Long orderId;
    int operation = OPERATION_BROWSE;

    public static AddressPaginationFragment newInstance(Long myGameId, Long targetGameId, Long targetUserId) {

        Bundle args = new Bundle();

        args.putLong(ARG_MY_GAME_ID, myGameId);
        args.putLong(ARG_TARGET_GAME_ID, targetGameId);
        args.putLong(ARG_TARGET_USER_ID, targetUserId);
        args.putInt(ARG_OPERATION, OPERATION_MATCH);

        AddressPaginationFragment fragment = new AddressPaginationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddressPaginationFragment newInstance(Long orderId) {

        Bundle args = new Bundle();

        args.putLong(ARG_ORDER_ID, orderId);
        args.putInt(ARG_OPERATION, OPERATION_CONFIRM);

        AddressPaginationFragment fragment = new AddressPaginationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddressPaginationFragment newInstance(){
        Bundle args = new Bundle();

        args.putInt(ARG_OPERATION, OPERATION_BROWSE);

        AddressPaginationFragment fragment = new AddressPaginationFragment();
        fragment.setArguments(args);
        return fragment;
    }



    private void retrieveFromArgs(Bundle bundle){
        myGameId = bundle.getLong(ARG_MY_GAME_ID, 0);
        targetGameId = bundle.getLong(ARG_TARGET_GAME_ID, 0);
        targetUserId = bundle.getLong(ARG_TARGET_USER_ID, 0);

        orderId = bundle.getLong(ARG_ORDER_ID, 0);

        OPERATION = bundle.getInt(ARG_OPERATION, OPERATION_BROWSE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        retrieveFromArgs(getArguments());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Call<List<AddressBean>> callApi() {
        return mGameTradeService.getAddressList(
                mUserId,
                currentPage,
                PAGE_SIZE
        );
    }

    @Override
    protected LinearPaginationAdapter<AddressBean> getNewAdapter(Fragment fragment, Bundle bundle) {

        return new AddressPaginationAdapter(fragment) {

            @Override
            public void onItemClicked(View v, Integer addressId) {

                switch(OPERATION){
                    case OPERATION_BROWSE:
                        //ConfirmMatch(gameId, targetUserId, addressId);
                        Intent intent = new Intent();
                        intent.putExtra("addressId", addressId.toString());
                        intent.putExtra("addressOperation", "modify");
                        intent.setClass(getActivity(), AddressOperationActivity.class);
                        startActivity(intent);
                        break;
                    case OPERATION_MATCH:
                        progressBar.setVisibility(View.VISIBLE);
                        disableTouchAndRolling();

                        mGameTradeService.confirmMatch(mUserId, myGameId,
                                new CreateOrderBean( targetGameId, targetUserId, addressId))
                                .enqueue(new Callback<TradeConfirmBean>() {
                                    @Override
                                    public void onResponse(Call<TradeConfirmBean> call, Response<TradeConfirmBean> response) {

                                        if(response.code() == 200) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            enableTouchAndRolling();

                                            Toast.makeText(getActivity(), "Order Successfully Created", Toast.LENGTH_SHORT)
                                                    .show();

                                            Intent intent = OrderActivity.newIntent(getContext(), OrderActivity.CALLER_ADDRESS);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "HTTP: " + response.code(), Toast.LENGTH_SHORT)
                                                    .show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            enableTouchAndRolling();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<TradeConfirmBean> call, Throwable t) {
                                        Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT)
                                                    .show();


                                        progressBar.setVisibility(View.INVISIBLE);
                                        enableTouchAndRolling();

                                    }
                                });
                        break;
                    case OPERATION_CONFIRM:
                        progressBar.setVisibility(View.VISIBLE);
                        disableTouchAndRolling();

                        mGameTradeService.confirmOrder(mUserId, orderId,
                                new OrderConfirmBean(addressId))
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        switch(response.code()){
                                            case 200:
                                                progressBar.setVisibility(View.INVISIBLE);
                                                enableTouchAndRolling();

                                                Toast.makeText(getActivity(), "Order Successfully Confirmed", Toast.LENGTH_SHORT)
                                                        .show();

                                                Intent intent = OrderActivity.newIntent(getContext(), OrderActivity.CALLER_ADDRESS);
                                                startActivity(intent);
                                                break;
                                            case 409:
                                                Toast.makeText(getActivity(), "Order already created. Jumping to order list...", Toast.LENGTH_LONG)
                                                        .show();
                                                intent = OrderActivity.newIntent(getContext(), OrderActivity.CALLER_ADDRESS);
                                                startActivity(intent);
                                                break;
                                            default:
                                                Toast.makeText(getActivity(), "HTTP: " + response.code(), Toast.LENGTH_SHORT)
                                                        .show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                enableTouchAndRolling();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT)
                                                .show();

                                        progressBar.setVisibility(View.INVISIBLE);
                                        enableTouchAndRolling();

                                    }
                                });
                        break;

                }
            }
        };
    }

}
