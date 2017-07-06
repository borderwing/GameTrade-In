package com.example.ye.gametrade_in;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public Integer userId ;
    public RelativeLayout menuUserDetailedHeader, menuDefaultHeader, mainMenuDetail;
    public TextView menuUserName;
    public Button menuRegisterButton, menuLoginButton, menuMyListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationIcon(R.drawable.nav);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        GridView gameGridView = (GridView) findViewById(R.id.gameGridView);
        ArrayList<HashMap<String, Object>> ListImageItem = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gameItemImage", R.drawable.gameicon);
            map.put("gameItemText", "NO."+String.valueOf(i+1));
            ListImageItem.add(map);
        }
        SimpleAdapter homeGameItems =  new SimpleAdapter
                (this, ListImageItem, R.layout.item, new String[]{"gameItemImage","gameItemText"}, new int[]{R.id.gameItemImage, R.id.gameItemText});
        gameGridView.setAdapter(homeGameItems);
        gameGridView.setOnItemClickListener(new gameItemClickListener());

        menuUserDetailedHeader = (RelativeLayout) findViewById(R.id.menuUserDetailedHeader);
        menuDefaultHeader = (RelativeLayout) findViewById(R.id.menuDefaultHeader);
        mainMenuDetail = (RelativeLayout) findViewById(R.id.mainMenuDetail);
        menuUserName= (TextView) findViewById(R.id.menuUserName);
        menuRegisterButton = (Button) findViewById(R.id.menuRegisterButton);
        menuLoginButton = (Button) findViewById(R.id.menuLoginButton);
        menuMyListButton = (Button) findViewById(R.id.menuMyListButton);

        menuRegisterButton.setOnClickListener(menuRegisterOnClickListener);
        menuLoginButton.setOnClickListener(menuLoginOnClickListener);
        menuMyListButton.setOnClickListener(menuMyListButtonOnClickListener);

        try{
            // get userId from LoginActivity. Default userId is 0

            Intent ToMainIntent = getIntent();
            userId = ToMainIntent.getIntExtra("userId" , 0);
            if(userId != 0){
                SetMenuHeaderUserDetailed();
                UserDetailTask userDetailTask = new UserDetailTask();
                userDetailTask.execute(userId.toString());
                //showDialog(userId.toString());
            }
            else if(userId == 0){
                //showDialog(userId.toString());
                SetMenuHeaderDefault();
            }
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }


    }

    private class gameItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3){
            Intent intent;
            intent = new Intent();
            intent.putExtra("gameId", String.valueOf(arg2+1));
            intent.putExtra("userId", String.valueOf(userId));
            intent.setClass(MainActivity.this, GameDetailActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            //HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            //setTitle((String)item.get("gameItemText"));
        }
    }

    private View.OnClickListener menuLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    private View.OnClickListener menuRegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    private View.OnClickListener menuMyListButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("userId", userId);
            intent.setClass(MainActivity.this, MyListActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    private class UserDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public UserDetailBean userDetail;
        public Boolean finish = false;

        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(String... params){
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/user/" + params[0];
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();
                userDetail = jsonProcessor.GetUserDetailBean(reader.readLine());
                finish = true;
                // status = "connected: "  + game.platform + " " + responseCode;
            }
            catch (Exception exc){
                exc.printStackTrace();
                // status = "Disconnected: " + responseCode;
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progresses)
        {
            super.onProgressUpdate(progresses);
        }
        @Override
        protected  void onPostExecute(String result)
        {
            SetUserDetailedLayout(userDetail.username);
            super.onPostExecute(result);
        }
    }


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
                case R.id.action_myList:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, MyListActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;
                case R.id.action_publish:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, PublishActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_gameDetail:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, GameDetailActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_register:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_orderDetail:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, OrderDetailActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_login:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_search:
                    message += "Click search";
                    // showDialog(userId.toString());
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    private void SetUserDetailedLayout(String userName){
        menuUserName.setText(userName);
    }

    private void SetMenuHeaderDefault(){
        mainMenuDetail.setVisibility(View.GONE);
        menuUserDetailedHeader.setVisibility(View.GONE);
        menuDefaultHeader.setVisibility(View.VISIBLE);
    }

    private void SetMenuHeaderUserDetailed(){
        menuDefaultHeader.setVisibility(View.GONE);
        menuUserDetailedHeader.setVisibility(View.VISIBLE);
        mainMenuDetail.setVisibility(View.VISIBLE);
    }


    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
