package com.example.ye.gametrade_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class GameDetailActivity extends AppCompatActivity {

    private ImageButton homebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamedetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.gameDetailToolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        homebutton = (ImageButton) findViewById(R.id.homeButton);
        homebutton.setOnClickListener(onHomeButtonListener);
    }

        private View.OnClickListener onHomeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
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
            switch (menuItem.getItemId()){
                case R.id.action_myList:
                    Intent intent = new Intent();
                    intent.setClass(GameDetailActivity.this, MyListActivity.class);
                    startActivity(intent);
                    GameDetailActivity.this.finish();
                    break;
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
