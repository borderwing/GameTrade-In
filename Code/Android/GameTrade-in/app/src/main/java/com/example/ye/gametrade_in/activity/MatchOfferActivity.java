package com.example.ye.gametrade_in.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameTransportBean;
import com.example.ye.gametrade_in.Bean.WishBean;
import com.example.ye.gametrade_in.MainActivity;
import com.example.ye.gametrade_in.QueryPreferences;
import com.example.ye.gametrade_in.R;
import com.example.ye.gametrade_in.fragment.MatchedOfferReloadableFragment;
import com.example.ye.gametrade_in.utils.GameDetailUtility;
import com.google.gson.Gson;
import com.travijuu.numberpicker.library.NumberPicker;

import org.w3c.dom.Text;

import static java.security.AccessController.getContext;

/**
 * Created by lykav on 9/12/2017.
 */

public class MatchOfferActivity extends AppCompatActivity {

    /*private static final String EXTRA_WISH = "MatchOfferActivity.wish";
    private static final String EXTRA_GAME_DETAIL = "MatchOfferActivity.game_detail";


    // fields for the upper area view
    private WishBean mWish;
    private GameDetailBean mGameDetail;

    private TextView mWishMeta;
    private TextView mWishTitle;

    private TextView mWishCredit;
    private NumberPicker mNumberPicker;

    private ImageView mCover;
    private ProgressBar mCoverProgress;

    private Button mButtonReload;

    private LinearLayout mDummy;


    public Intent newIntent(Context context, WishBean wish, GameDetailBean gameDetail){
        Intent intent = new Intent(context, MatchOfferActivity.class);
        Gson gson = new Gson();
        String wishJson = gson.toJson(wish);
        String gameDetailJson = gson.toJson(gameDetail);

        intent.putExtra(EXTRA_WISH, wishJson);
        intent.putExtra(EXTRA_GAME_DETAIL, gameDetailJson);

        return intent;

    }

    public WishBean getWishFromIntent(Intent intent){
        String wishString = intent.getStringExtra(EXTRA_WISH);
        if(wishString == null){
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(wishString, WishBean.class);
    }

    public GameDetailBean getGameDetailFromIntent(Intent intent){
        String gameDetailString = intent.getStringExtra(EXTRA_GAME_DETAIL);
        if(gameDetailString == null){
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(gameDetailString, GameDetailBean.class);
    }



    Fragment createFragment(){
        return new MatchedOfferReloadableFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_matching);

        mWish = getWishFromIntent(getIntent());
        mGameDetail = getGameDetailFromIntent(getIntent());


        mWishMeta = (TextView) findViewById(R.id.item_wish_meta);
        mWishTitle = (TextView) findViewById(R.id.item_wish_title);

        mWishCredit = (TextView) findViewById(R.id.item_wish_credit);
        mNumberPicker = (NumberPicker) findViewById(R.id.detail_credit_picker);

        mCover = (ImageView) findViewById(R.id.item_wish_cover);
        mCoverProgress = (ProgressBar) findViewById(R.id.item_cover_progress);

        mButtonReload = (Button) findViewById(R.id.item_reload_offer);

        mDummy = (LinearLayout) findViewById(R.id.dummy_id);

        GameDetailUtility utility = new GameDetailUtility(mGameDetail);
        String meta = utility.getPlatformString(mWish.getGame().getPlatformId()) + " | " +
                utility.getRegionString(mWish.getGame().getRegionId());
        mWishMeta.setText(meta);
        mWishTitle.setText(mGameDetail.getTitle());
        mNumberPicker.setValue(mWish.getPoints());

        // TODO: add estimated points here
        mWishCredit.setVisibility(View.INVISIBLE);

        mButtonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);


        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    protected GameTransportBean verifyNewPoint(){
        int editedCredit = mNumberPicker.getValue();
        String userIdStr = QueryPreferences.
                getStoredUserIdQuery(getApplicationContext());

        if(userIdStr == null){
            Toast.makeText(this,
                    R.string.login_prompt,
                    Toast.LENGTH_SHORT).show();
            return null;
        } else if(mNumberPicker.hasFocus()){
            clearPickerFocus();
            return null;
        }  else if(!mNumberPicker.valueIsAllowed(editedCredit)){
            Toast.makeText(this,
                    R.string.invalid_credit, Toast.LENGTH_SHORT).show();
            return null;
        } else {
            GameTransportBean gameTransport =
                    new GameTransportBean(
                            mIgdbId,
                            mSelectedPlatform.getPlatformId(),
                            mSelectedRegion.getRegionId(),
                            editedCredit);

            return gameTransport;
        }
    }*/
}
