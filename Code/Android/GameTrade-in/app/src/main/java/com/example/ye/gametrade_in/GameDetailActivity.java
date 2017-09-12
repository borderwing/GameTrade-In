package com.example.ye.gametrade_in;

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

    private ImageButton homebutton;
    private String gameId, userId, operation, wishPoints, offerPoints;
    private Long igdbId;
    GameTradeInApplication gameTradeInApplication;
    BitmapBean bitmapBean;
    Bitmap bitmap;

    public static Intent newInent(Context packageContext, Long igdbId){
        Intent intent = new Intent(packageContext, GameDetailActivity.class);
        intent.putExtra(EXTRA_IGDB_ID, igdbId);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        return FragmentGameDetail.newInstance(igdbId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        igdbId = intent.getLongExtra(EXTRA_IGDB_ID, 0);

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
