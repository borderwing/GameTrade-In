package com.example.ye.gametrade_in;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.utils.SingleFragmentActivity;

public class GameDetailActivity extends SingleFragmentActivity {

    public static final String EXTRA_IGDB_ID =
            "com.example.ye.gametrade_in.igdb_id";

    public static final String EXTRA_GAME_ID =
            "com.example.ye.gametrade_in.game_id";

    public static final String EXTRA_PLATFORM_ID =
            "com.example.ye.gametrade_in.platform_id";
    public static final String EXTRA_REGION_ID =
            "com.example.ye.gametrade_in.region_id";

    public static final String EXTRA_OPERATION =
            "com.example.ye.gametrade-in.extra_from";

    public static final int OPERATION_WISH = 1;
    public static final int OPERATION_OFFER = 2;
    public static final int OPERATION_BROWSE = 0;

    private int OPERATION = OPERATION_BROWSE;

    private ImageButton homebutton;


    // private String gameId, userId, operation, wishPoints, offerPoints;
    private Long igdbId, gameId;

    public int platformId, regionId;

    GameTradeInApplication gameTradeInApplication;
    BitmapBean bitmapBean;
    Bitmap bitmap;



    public static Intent newIntent(Context packageContext, Long igdbId){
        Intent intent = new Intent(packageContext, GameDetailActivity.class);
        intent.putExtra(EXTRA_IGDB_ID, igdbId);
        intent.putExtra(EXTRA_OPERATION, OPERATION_BROWSE);
        return intent;
    }

    public static Intent newIntent(Context packageContext, Long igdbId, Long gameId,
                                   int platformId, int regionId, int operation){
        Intent intent = new Intent(packageContext, GameDetailActivity.class);
        intent.putExtra(EXTRA_IGDB_ID, igdbId);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        intent.putExtra(EXTRA_PLATFORM_ID, platformId);
        intent.putExtra(EXTRA_REGION_ID, regionId);

        intent.putExtra(EXTRA_OPERATION, operation);
        return intent;
    }


    @Override
    protected Fragment createFragment() {

        return FragmentGameDetail.newInstance(igdbId, gameId, OPERATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        igdbId = intent.getLongExtra(EXTRA_IGDB_ID, 0);
        gameId = intent.getLongExtra(EXTRA_GAME_ID, 0);
        platformId = intent.getIntExtra(EXTRA_PLATFORM_ID,0);
        regionId = intent.getIntExtra(EXTRA_REGION_ID, 0);

        OPERATION = intent.getIntExtra(EXTRA_OPERATION, 0);

        super.onCreate(savedInstanceState);

        //bundle.putString("wishPoints", wishPoints);
        //bundle.putString("offerPoints", offerPoints);
//
//        fragmentGameDetail.setArguments(bundle);
//        transaction.add(R.id.layoutGameDetail, fragmentGameDetail);
//        transaction.commit();
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.gameDetailToolBar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.inflateMenu(R.menu.toolbar);
//        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

//        private View.OnClickListener onHomeButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.putExtra("userId", Integer.valueOf(userId));
//            intent.setClass(GameDetailActivity.this, MainActivity.class);
//            startActivity(intent);
//            GameDetailActivity.this.finish();
//            }
//        };



//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.toolbar, menu);
//        return true;
//    }
//
//    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
//    {
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem)
//        {
//            String message = "";
//            Intent intent;
//            switch (menuItem.getItemId()){
//                case R.id.action_search:
//                    message += "Click search";
//                    break;
//                case R.id.action_settings:
//                    message += "Click setting";
//                    break;
//                case R.id.action_HomeButton:
//                    intent = new Intent();
//                    intent.setClass(GameDetailActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    GameDetailActivity.this.finish();
//                    break;
//            }
//            if(!message.equals(""))
//            {
//                Toast.makeText(GameDetailActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//            return true;
//        }
//    };
//
//
//    /*****************************************************************************************/
//    /* Helper function */
//
//    private void showDialog(String msg){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int id){
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}
