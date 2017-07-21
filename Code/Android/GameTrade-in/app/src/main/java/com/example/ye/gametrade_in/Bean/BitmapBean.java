package com.example.ye.gametrade_in.Bean;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ye on 2017/7/19.
 */

public class  BitmapBean {

    transient public Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
