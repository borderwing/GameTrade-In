package com.example.ye.gametrade_in;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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

public class OfferListActivity extends AppCompatActivity{

    TextView offerListTitle;
    GameTradeInApplication gameTradeInApplication;
    Integer userId;
    OfferListBean[] offerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        offerListTitle =(TextView) findViewById(R.id.myListTitle);
        offerListTitle.setText("My Offer List");
        Toolbar toolbar = (Toolbar) findViewById(R.id.myListToolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        gameTradeInApplication = (GameTradeInApplication) getApplication();
        userId = gameTradeInApplication.GetLoginUser().getUserId();
        ImageButton button = (ImageButton) findViewById(R.id.homeButton);
        button.setOnClickListener(listener);
        MyOfferListDetailTask myOfferListDetailTask = new MyOfferListDetailTask();
        myOfferListDetailTask.execute(userId.toString());

    }
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
                (this, ListImageItem, R.layout.item, new String[]{"gameItemImage","gameItemText"}, new int[]{R.id.gameItemImage, R.id.gameItemText});
        myListGridView.setAdapter(homeGameItems);
        myListGridView.setOnItemClickListener(new gameItemClickListener());
    }

    private class MyOfferListDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        // public MyListBean[] myList;
        public Boolean finish = false;

        @Override
        protected  void onPreExecute(){
        }
        @Override
        protected  String doInBackground(String... params){
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/user/" + userId.toString() + "/offer";
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();

                status = reader.readLine();

                offerList = jsonProcessor.GetMyOfferListBean(status);
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
            showList(offerList.length);
            super.onPostExecute(result);
        }
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


    private class gameItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3){
            Intent intent;
            intent = new Intent();

            intent.putExtra("gameId", String.valueOf(offerList[arg2].getPair().gameId));
            intent.setClass(OfferListActivity.this, GameDetailActivity.class);
            startActivity(intent);
            OfferListActivity.this.finish();
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(OfferListActivity.this, MainActivity.class);
            startActivity(intent);
            OfferListActivity.this.finish();
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
                    intent.setClass(OfferListActivity.this, MainActivity.class);
                    startActivity(intent);
                    OfferListActivity.this.finish();
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(OfferListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
