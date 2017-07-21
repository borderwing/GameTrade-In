package com.example.ye.gametrade_in;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.UserBean;
import com.example.ye.gametrade_in.Bean.UserDetailBean;
import com.example.ye.gametrade_in.Listener.AutoLoadListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    public Integer userId ;
    public RelativeLayout menuUserDetailedHeader, menuDefaultHeader, mainMenuDetail;
    public TextView menuUserName;
    public Button menuRegisterButton, menuLoginButton, menuLogoutButton, menuMyListButton, menuMyOfferListButton, menuMyAddressButton;
    public GameTradeInApplication gameTradeInApplication;
    int limit;
    String serverUrl;
    GameTileBean[] gameTileBeanList;
    BitmapBean[] bitmapBeanList;
    boolean finish;
    WorkCounter workCounter = new WorkCounter();

    private static final int THREAD_NUM = 2;
    private static CountDownLatch countDownLatch = null;

    private Notification notification;
    private NotificationManager notificationManager;
    private int i = 0;
    private int k = 0;

    String authorizedHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        gameTradeInApplication = (GameTradeInApplication) getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        limit = gameTradeInApplication.getLimit();

        bitmapBeanList = new BitmapBean[limit];

        for(int p = 0; p < limit; ++p){
            bitmapBeanList[p] = new BitmapBean();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationIcon(R.drawable.nav);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);


        // this part is for notification
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, RegisterActivity.class), 0);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.nav)
                .setContentTitle("Test")
                .setContentText("Please register!")
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        notification = builder.build();
        notificationManager.notify(i, notification);

        // workCounter.setRunningTasks(10);

        GetGameTileDetail();

        /*while (workCounter.getRunningTasks() != 0){

        }*/

        // Grid View
        GridView gameGridView = (GridView) findViewById(R.id.gameGridView);
        ArrayList<HashMap<String, Object>> ListImageItem = new ArrayList<HashMap<String, Object>>();
        for(i = 0; i < limit; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            try {
                map.put("gameItemImage", bitmapBeanList[i].getBitmap());
                map.put("gameItemText", gameTileBeanList[i].getTitle());
                ListImageItem.add(map);
            }
            catch (Exception exc){
                showDialog(exc.toString());
            }
        }



        try {
            SimpleAdapter homeGameItems = new SimpleAdapter
                    (this,
                            ListImageItem,
                            R.layout.item,
                            new String[]{"gameItemImage", "gameItemText"},
                            new int[]{R.id.gameItemImage, R.id.gameItemText});

            homeGameItems.setViewBinder(new SimpleAdapter.ViewBinder(){
                @Override
                public boolean setViewValue(View view, Object bitmapData, String s) {
                    if (view instanceof ImageView && bitmapData instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) bitmapData);
                        return true;
                    }
                    return false;
                }
            });
            gameGridView.setAdapter(homeGameItems);
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }
        gameGridView.setOnItemClickListener(new gameItemClickListener());

        // AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);
        // gameGridView.setOnScrollListener(autoLoadListener);

        // Declaration
        menuUserDetailedHeader = (RelativeLayout) findViewById(R.id.menuUserDetailedHeader);
        menuDefaultHeader = (RelativeLayout) findViewById(R.id.menuDefaultHeader);
        mainMenuDetail = (RelativeLayout) findViewById(R.id.mainMenuDetail);
        menuUserName= (TextView) findViewById(R.id.menuUserName);
        menuRegisterButton = (Button) findViewById(R.id.menuRegisterButton);
        menuLoginButton = (Button) findViewById(R.id.menuLoginButton);
        menuLogoutButton = (Button) findViewById(R.id.menuLogoutButton);
        menuMyListButton = (Button) findViewById(R.id.menuMyListButton);
        menuMyOfferListButton = (Button) findViewById(R.id.menuMyOfferListButton);
        menuMyAddressButton = (Button) findViewById(R.id.menuMyAddressButton);

        menuRegisterButton.setOnClickListener(menuRegisterOnClickListener);
        menuLoginButton.setOnClickListener(menuLoginOnClickListener);
        menuLogoutButton.setOnClickListener(menuLogoutOnClickListener);
        menuMyListButton.setOnClickListener(menuMyListButtonOnClickListener);
        menuMyOfferListButton.setOnClickListener(menuMyOfferListButtonOnClickListener);
        menuMyAddressButton.setOnClickListener(menuMyAddressButtonOnClickListener);


        // set userId
        try{
            userId = gameTradeInApplication.GetLoginUser().getUserId();

            if(userId == null){
                UserBean userDefault = new UserBean();
                userDefault.setUserId(0);
                gameTradeInApplication.SetUserLogin(userDefault);
                userId = 0;
            }
            if(userId != 0){
                authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());
                SetMenuHeaderUserDetailed();
                UserDetailTask userDetailTask = new UserDetailTask();
                userDetailTask.execute(userId.toString());
            }
            else if(userId == 0){
                SetMenuHeaderDefault();
            }
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }
    }

    /*****************************************************************************************/
    /* Grid View Scroll Listener*/


    AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {

        public void execute() {
            showDialog("Bottom");

        }
    };

    /*****************************************************************************************/
    /* Button Listener settings */

    private class gameItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3){
            Intent intent;
            intent = new Intent();
            intent.putExtra("igdbId", gameTileBeanList[arg2].getIgdbId());
            intent.putExtra("operation", "browse");
            intent.putExtra("gameBitmap",(Bitmap) bitmapBeanList[arg2].getBitmap());
            intent.setClass(MainActivity.this, GameDetailActivity.class);
            startActivity(intent);
        }
    }


    private View.OnClickListener menuLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener menuRegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener menuLogoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            gameTradeInApplication.SetUserLogout();
            gameTradeInApplication.SetUserAuthenticationOut();
            intent.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };


    private View.OnClickListener menuMyListButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MyListActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener menuMyOfferListButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, OfferListActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener menuMyAddressButtonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent = new Intent();
            intent.putExtra("operation", "browse");
            intent.setClass(MainActivity.this, AddressActivity.class);
            startActivity(intent);
        }
    };



    /*****************************************************************************************/
    /* Part for gameTile detail */


    private class GameTileDetailTask extends AsyncTask<String, Integer, String> {
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


                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("game")
                        .appendPath("trending")
                        .appendQueryParameter("limit", String.valueOf(limit))
                        .appendQueryParameter("offset", "0");

                //urlStr = serverUrl + "api/game/trending?limit="+String.valueOf(limit)+"&offset=0";
                urlStr = serverUrl + builder.build().toString();
                //urlStr = builder.build().toString();
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestProperty("Authorization", authorizedHeader);
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();
                gameTileBeanList = jsonProcessor.GetGameTileListBean(reader.readLine());
                finish = true;
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Failed";
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
            super.onPostExecute(result);
        }
    }



    private class GameTileImageTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public Boolean finish = false;
        Bitmap bmp ;



        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(String... params){
            HttpURLConnection urlConn;
            try {

                urlStr = params[0];

                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                //urlConn.setRequestProperty("Authorization", authorizedHeader);

                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();

                Log.d("log", String.valueOf(k)+"get bitmap start");
                // BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                bmp = BitmapFactory.decodeStream(in);

                bitmapBeanList[k].setBitmap(bmp);

                in.close();
                Log.d("log", String.valueOf(k)+"get bitmap finished");


                /*countDownLatch.countDown();*/
                // finish = true;
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Failed";
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

            super.onPostExecute(result);
            // workCounter.getRunningTasks();
            // workCounter.runningTasksRelease();
        }
    }




    public void GetGameTileDetail(){
        MainActivity.GameTileDetailTask gameTileDetailTask = new MainActivity.GameTileDetailTask();
        try {
            String test = gameTileDetailTask.execute().get();
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }

        countDownLatch = new CountDownLatch(THREAD_NUM-1);

        for(k = 0; k < limit; k++)
        {
            try {

                String CoverUrl = gameTileBeanList[k].getCoverUrl();
                MainActivity.GameTileImageTask gameTileImageTask = new MainActivity.GameTileImageTask();
                // gameTileImageTask.execute(CoverUrl);
                // asyncTasks.add(gameTileImageTask);

                //Log.d("log", "thread:"+String.valueOf(k)+"start");

                //new GameTileImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CoverUrl);

                // Log.d("log", "thread:"+String.valueOf(k)+"out");

                // new GameTileImageTask().execute(CoverUrl);
                // gameTileImageTask.execute(CoverUrl);

                String test = gameTileImageTask.execute(CoverUrl).get();

                // countDownLatch.await();

            }
            catch (Exception exc){
                showDialog(exc.toString());
            }
        }

        /*try{
            Log.d("log","main wait");
            countDownLatch.await();
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }
        Log.d("log","main continue");*/
        // workCounter.getRunningTasks();

        //while (workCounter.getRunningTasks()!= 0){

        //}

    }




    /*****************************************************************************************/
    /* Part for user detail */


    private void SetUserDetailedLayout(String userName){
        menuUserName.setText(userName);
    }


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
                urlStr = serverUrl + "api/user/" + params[0];
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                urlConn.setRequestProperty("Authorization", authorizedHeader);

                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();
                userDetail = jsonProcessor.GetUserDetailBean(reader.readLine());
                finish = true;
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Failed";
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




    /*****************************************************************************************/
    /* Part for toolbar menu */


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
                case R.id.action_publish:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, PublishActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;
                case R.id.action_orderDetail:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, OrderDetailActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;
                case R.id.action_search:
                    message += "Click search";
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


    /*****************************************************************************************/
    /* Main menu setting */

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
}
