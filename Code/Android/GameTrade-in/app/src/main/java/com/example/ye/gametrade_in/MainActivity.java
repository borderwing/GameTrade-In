package com.example.ye.gametrade_in;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameTileBean;
import com.example.ye.gametrade_in.Bean.UserBean;
import com.example.ye.gametrade_in.Bean.UserDetailBean;
import com.example.ye.gametrade_in.Listener.AutoLoadListener;
import com.example.ye.gametrade_in.api.GameTradeApi;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.utils.PaginationAdapterCallback;
import com.example.ye.gametrade_in.utils.PaginationScrollListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {

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
    WorkCounter workCounter = new WorkCounter();

    private static final int THREAD_NUM = 2;
    private static CountDownLatch countDownLatch = null;

    private Notification notification;
    private NotificationManager notificationManager;
    private int i = 0;
    private int k = 0;

    String authorizedHeader;


    /*
        Fields for RecyclerView & error UI
     */
    private TilePaginationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    /*
        Fields for pagination & API fetching
     */
    private static final int PAGE_START = 0;
    private static final int TOTAL_PAGES = 5;
    private static final int PAGE_SIZE = 10;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int currentPage = PAGE_START;

    private GameTradeService mGameTradeService;



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


        /*
            setup recyclerView & pagination
        */
        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        mAdapter = new TilePaginationAdapter(this);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(mAdapter);

        rv.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        mGameTradeService = GameTradeApi.getClient().create(GameTradeService.class);

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });



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

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        callTrendingGamesApi().enqueue(new Callback<List<GameTileBean>>() {
            @Override
            public void onResponse(Call<List<GameTileBean>> call, Response<List<GameTileBean>> response) {
                // Got data. Send it to adapter

                hideErrorView();

                List<GameTileBean> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                mAdapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<GameTileBean>> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    /**
     * @param response extracts List<{@link GameTileBean>} from response
     * @return
     */
    private List<GameTileBean> fetchResults(Response<List<GameTileBean>> response) {
        return response.body();
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callTrendingGamesApi().enqueue(new Callback<List<GameTileBean>>() {
            @Override
            public void onResponse(Call<List<GameTileBean>> call, Response<List<GameTileBean>> response) {
                mAdapter.removeLoadingFooter();
                isLoading = false;

                List<GameTileBean> results = fetchResults(response);
                mAdapter.addAll(results);

                if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<GameTileBean>> call, Throwable t) {
                t.printStackTrace();
                mAdapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }


    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<List<GameTileBean>> callTrendingGamesApi() {
        return mGameTradeService.getTrendingGames(
                PAGE_SIZE,
                currentPage * PAGE_SIZE
        );
    }


    @Override
    public void retryPageLoad() {
        loadNextPage();
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
        menu.findItem(R.id.action_search).setVisible(true);
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
                    intent = new Intent();
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
