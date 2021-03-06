package com.example.ye.gametrade_in;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.MyListBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MyListActivity extends AppCompatActivity{
    TextView myListTitle;
    GameTradeInApplication gameTradeInApplication;
    Integer userId;
    MyListBean[] myList;
    String serverUrl;
    String authorizedHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);

        gameTradeInApplication = (GameTradeInApplication) getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        userId = gameTradeInApplication.GetLoginUser().getUserId();
        // authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());
        authorizedHeader = QueryPreferences.getStoredAuthorizedQuery(getApplicationContext());

        myListTitle =(TextView) findViewById(R.id.myListTitle);
        myListTitle.setText("My Wish List");

        MyListDetailTask myListDetailTask = new MyListDetailTask();
        myListDetailTask.execute(userId.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.myListToolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    /*****************************************************************************************/
    /* Part my wish list task*/



    public void showList(Integer showNum){
        GridView myListGridView = (GridView) findViewById(R.id.myListGridView);
        ArrayList<HashMap<String, Object>> ListImageItem = new ArrayList<HashMap<String, Object>>();
        for(int i = 0; i < showNum; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gameItemImage", R.drawable.gameicon);
            map.put("gameItemText", "NO."+String.valueOf(i+1));
            ListImageItem.add(map);
        }
        SimpleAdapter homeGameItems =  new SimpleAdapter
                (this, ListImageItem, R.layout.item_game_tile, new String[]{"gameItemImage","gameItemText"}, new int[]{R.id.item_tile_image, R.id.item_tile_text});
        myListGridView.setAdapter(homeGameItems);
        myListGridView.setOnItemClickListener(new gameItemClickListener());
    }



    private class MyListDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public Boolean finish = false;
        @Override
        protected  void onPreExecute(){
        }
        @Override
        protected  String doInBackground(String... params){
            HttpURLConnection urlConn;
            try {
                urlStr = serverUrl + "api/user/" + userId.toString() + "/wishlist";
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                urlConn.setRequestProperty("Authorization", authorizedHeader);

                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();
                status = reader.readLine();
                myList = jsonProcessor.GetMyListBean(status);
                finish = true;
            }
            catch (Exception exc){
                exc.printStackTrace();
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
            if(myList == null){
                showDialog("No game in your wish list.");
            }
            else {
                showList(myList.length);
            }
            super.onPostExecute(result);
        }
    }



    /*****************************************************************************************/
    /* Helper function */

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




    /*****************************************************************************************/
    /* Click listener */


    private class gameItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3){
            Intent intent;
            intent = new Intent();
            intent.putExtra("operation","wishList");
            intent.putExtra("gameId", String.valueOf(myList[arg2].getPair().gameId));
            intent.setClass(MyListActivity.this, GameDetailActivity.class);
            startActivity(intent);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MyListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };




    /*****************************************************************************************/
    /* Part for toolBar */

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


    /*****************************************************************************************/


}
