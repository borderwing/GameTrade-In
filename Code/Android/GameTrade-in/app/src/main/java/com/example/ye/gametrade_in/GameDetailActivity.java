package com.example.ye.gametrade_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class GameDetailActivity extends AppCompatActivity {

    private ImageButton homebutton;
    private String gameId, userId, operation, wishPoints, offerPoints;
    GameTradeInApplication gameTradeInApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamedetail);
        gameTradeInApplication =(GameTradeInApplication) getApplication();
        Intent intent = getIntent();
        gameId = intent.getStringExtra("gameId");
        operation = intent.getStringExtra("operation");
        // wishPoints = intent.getStringExtra("wishPoints");
        // offerPoints = intent.getStringExtra("offerPoints");
        gameTradeInApplication.GetLoginUser();

        userId = String.valueOf(gameTradeInApplication.GetLoginUser().getUserId());

        // Build a new fragment and set variable to its bundle
        FragmentGameDetail fragmentGameDetail = new FragmentGameDetail();
        android.app.FragmentManager manager = getFragmentManager();
        android.app.FragmentTransaction transaction =manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("gameId", gameId);
        bundle.putString("userId", userId);
        bundle.putString("operation", operation);
        //bundle.putString("wishPoints", wishPoints);
        //bundle.putString("offerPoints", offerPoints);
        fragmentGameDetail.setArguments(bundle);
        transaction.add(R.id.layoutGameDetail, fragmentGameDetail);
        transaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.gameDetailToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

        private View.OnClickListener onHomeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("userId", Integer.valueOf(userId));
            intent.setClass(GameDetailActivity.this, MainActivity.class);
            startActivity(intent);
            GameDetailActivity.this.finish();
            }
        };




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String message = "";
            Intent intent;
            switch (menuItem.getItemId()){
                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
                case R.id.action_HomeButton:
                    intent = new Intent();
                    intent.setClass(GameDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    GameDetailActivity.this.finish();
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(GameDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
