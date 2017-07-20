package com.example.ye.gametrade_in.Listener;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

public class AutoLoadListener implements AbsListView.OnScrollListener {

    public interface AutoLoadCallBack {
        void execute();
    }

    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;

    private AutoLoadCallBack mCallback;

    public AutoLoadListener(AutoLoadCallBack callback) {
        this.mCallback = callback;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                View v = (View) view.getChildAt(view.getChildCount() - 1);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int y = location[1];

                if (view.getLastVisiblePosition() != getLastVisiblePosition && lastVisiblePositionY != y)
                {
                    Toast.makeText(view.getContext(), "Bottom", Toast.LENGTH_SHORT).show();
                    getLastVisiblePosition = view.getLastVisiblePosition();
                    lastVisiblePositionY = y;
                    return;
                } else if (view.getLastVisiblePosition() == getLastVisiblePosition && lastVisiblePositionY == y)
                {
                    mCallback.execute();
                }
            }
            getLastVisiblePosition = 0;
            lastVisiblePositionY = 0;
        }
    }
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
    }
}
