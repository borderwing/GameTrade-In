package com.example.ye.gametrade_in.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.GameDetailActivity;
import com.example.ye.gametrade_in.R;

/**
 * Created by lykav on 9/13/2017.
 */

public abstract class AddressPaginationAdapter extends LinearPaginationAdapter<AddressBean> {

    public abstract void onItemClicked(View v, Integer addressId);

    public AddressPaginationAdapter(Context context) {
        super(context);
    }

    public AddressPaginationAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    protected ItemHolder newItemHolder(View itemView) {
        return new AddressPaginationAdapter.AddressHolder(itemView);
    }

    @Override
    protected AddressBean newItem() {
        return new AddressBean();
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_address;
    }

    private class AddressHolder extends ItemHolder {
        private Integer mAddressId;

        private TextView mReceiver;
        private TextView mPhone;
        private TextView mRegion;
        private TextView mAddress;


        public AddressHolder(View itemView) {
            super(itemView);

            mReceiver = (TextView) itemView.findViewById(R.id.item_address_receiver);
            mPhone = (TextView) itemView.findViewById(R.id.item_address_phone);
            mRegion = (TextView) itemView.findViewById(R.id.item_address_region);
            mAddress = (TextView) itemView.findViewById(R.id.item_address_address);
        }

        public void bind(AddressBean address){
            mAddressId = address.getAddressId();

            mReceiver.setText(address.getReceiver());
            mPhone.setText(address.getPhone());
            mRegion.setText(address.getRegion());
            mAddress.setText(address.getAddress());
        }


        @Override
        public void onClick(View v) {
            onItemClicked(v, mAddressId);
        }
    }
}
