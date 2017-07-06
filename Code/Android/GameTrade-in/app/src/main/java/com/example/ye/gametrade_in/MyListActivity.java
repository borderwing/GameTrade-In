package com.example.ye.gametrade_in;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.myListToolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);


        GridView myListGridView = (GridView) findViewById(R.id.myListGridView);
        ArrayList<HashMap<String, Object>> ListImageItem = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gameItemImage", R.drawable.gameicon);
            map.put("gameItemText", "NO."+String.valueOf(i+1));
            ListImageItem.add(map);
        }
        SimpleAdapter homeGameItems =  new SimpleAdapter
                (this, ListImageItem, R.layout.item, new String[]{"gameItemImage","gameItemText"}, new int[]{R.id.gameItemImage, R.id.gameItemText});
        myListGridView.setAdapter(homeGameItems);
        myListGridView.setOnItemClickListener(new gameItemClickListener());


        ImageButton button = (ImageButton) findViewById(R.id.homeButton);
        button.setOnClickListener(listener);
    }

    private class gameItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3){
            Intent intent;
            intent = new Intent();
            intent.putExtra("gameId", String.valueOf(arg2+1));
            intent.setClass(MyListActivity.this, GameDetailActivity.class);
            startActivity(intent);
            MyListActivity.this.finish();
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MyListActivity.this, MainActivity.class);
            startActivity(intent);
            MyListActivity.this.finish();
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
                    intent.setClass(MyListActivity.this, MainActivity.class);
                    startActivity(intent);
                    MyListActivity.this.finish();
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(MyListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
