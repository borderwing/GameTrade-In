package com.example.ye.gametrade_in;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.UserLoginBean;
import com.example.ye.gametrade_in.Bean.UserDetailBean;
import com.example.ye.gametrade_in.Listener.AutoLoadListener;
import com.example.ye.gametrade_in.fragment.GameTilePaginationFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";

    public Integer userId ;
    public RelativeLayout menuUserDetailedHeader, menuDefaultHeader, mainMenuDetail;
    public TextView menuUserName;
    public Button menuRegisterButton, menuLoginButton, menuLogoutButton,
                  menuMyListButton, menuMyOfferListButton, menuMyAddressButton;

    public GameTradeInApplication gameTradeInApplication;

    int limit;
    String serverUrl;
    GameTileBean[] gameTileBeanList;
    BitmapBean[] bitmapBeanList;
    boolean finish;

    private Notification notification;
    private NotificationManager notificationManager;
    private int i = 0;
    private int k = 0;

    String authorizedHeader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameTradeInApplication = (GameTradeInApplication) getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        limit = gameTradeInApplication.getLimit();

        bitmapBeanList = new BitmapBean[limit];

        for(int p = 0; p < limit; ++p){
            bitmapBeanList[p] = new BitmapBean();
        }


        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_fragment_container);

        if(fragment == null){
            fragment = new GameTilePaginationFragment();
            fm.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .commit();
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.inflateMenu(R.menu.toolbar);

        toolbar.setNavigationIcon(R.drawable.nav);

         //toolbar.setOnMenuItemClickListener(onMenuItemClickListener);


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

            // userId = gameTradeInApplication.GetLoginUser().getUserId();

            if(QueryPreferences.getStoredUserIdQuery(getApplicationContext()) == null){
                UserLoginBean userDefault = new UserLoginBean();
                userDefault.setUserId(0);
                gameTradeInApplication.SetUserLogin(userDefault);
                userId = 0;
            }

            else {

                authorizedHeader = QueryPreferences.getStoredAuthorizedQuery(getApplicationContext());
                userId = Integer.valueOf(QueryPreferences.getStoredUserIdQuery(getApplicationContext()));
            }
            // userId = Integer.valueOf(QueryPreferences.getStoredQuery(getApplicationContext()));

            if(userId == null){
                UserLoginBean userDefault = new UserLoginBean();
                userDefault.setUserId(0);
                gameTradeInApplication.SetUserLogin(userDefault);
                userId = 0;
            }

            if(userId != 0){
                // authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());
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
            intent.putExtra("igdbId", String.valueOf(gameTileBeanList[arg2].getIgdbId()));
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

            QueryPreferences.setStoredQuery(getApplicationContext(), null, null);

            intent.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };


    private View.OnClickListener menuMyListButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, WishListActivity.class);
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
                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("user")
                        .appendPath("");
                urlStr = serverUrl + builder.build().toString() + params[0];
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
            if(userDetail != null){
                SetUserDetailedLayout(userDetail.username);
            }
            super.onPostExecute(result);
        }
    }




    /*****************************************************************************************/
    /* Part for toolbar menu */


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar, menu);

        SearchView search = (SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class))
        );
        search.setQueryHint(getResources().getString(R.string.search_hint));

        // menu.findItem(R.id.action_search).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "";
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_publish:
                intent = new Intent();
                intent.setClass(MainActivity.this, PublishActivity.class);
                startActivity(intent);
//                MainActivity.this.finish();
                return true;
            case R.id.action_orderDetail:
                intent = new Intent();
                intent.setClass(MainActivity.this, OrderDetailActivity.class);
                startActivity(intent);
//                MainActivity.this.finish();
                return true;
            case R.id.action_settings:
                message += "Click setting";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



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
