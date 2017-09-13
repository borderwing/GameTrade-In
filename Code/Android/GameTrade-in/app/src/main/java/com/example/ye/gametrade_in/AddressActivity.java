package com.example.ye.gametrade_in;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.TradeConfirmBean;
import com.example.ye.gametrade_in.Bean.temp.CreateOrderBean;
import com.example.ye.gametrade_in.activity.OrderActivity;
import com.example.ye.gametrade_in.api.GameTradeApi;
import com.example.ye.gametrade_in.api.GameTradeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ye on 2017/7/11.
 */

public class AddressActivity extends AppCompatActivity {

    private static final String EXTRA_MY_GAME_ID = "AddressActivity.EXTRA_MY_GAME_ID";
    private static final String EXTRA_TARGET_GAME_ID =  "AddressActivity.EXTRA_TARGET_GAME_ID";
    private static final String EXTRA_TARGET_USER_ID =  "AddressActivity.EXTRA_TARGET_USER_ID";
    private static final String EXTRA_OPERATION = "AddressActivity.EXTRA_OPERATION";

    private static final int OPERATION_MATCH = 1;
    private static final int OPERATION_BROWSE = 0;


    GameTradeInApplication gameTradeInApplication;
    String gameDetailId;

//    private String gameId, targetUserId, operation;

    private Long myGameId;
    private Long targetGameId;
    private Long targetUserId;
    private int operation;

    String serverUrl;
    Integer userId;
    boolean canChoose;
    private AddressBean[] addressBean;
    private ListView listView;
    ProgressBar mMainProgress;
    String authorizedHeader;
    Button addressOperationButton;


    private GameTradeService mGameTradeService;

    public static Intent newIntent(Context context, Long myGameId, Long targetGameId, Long targetUserId){
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(EXTRA_MY_GAME_ID, myGameId);
        intent.putExtra(EXTRA_TARGET_GAME_ID, targetGameId);
        intent.putExtra(EXTRA_TARGET_USER_ID, targetUserId);
        intent.putExtra(EXTRA_OPERATION, OPERATION_MATCH);

        return intent;
    }


    private void retrieveFromIntent(Intent intent){
        myGameId = intent.getLongExtra(EXTRA_MY_GAME_ID, 0);
        targetGameId = intent.getLongExtra(EXTRA_TARGET_GAME_ID, 0);
        targetUserId = intent.getLongExtra(EXTRA_TARGET_USER_ID, 0);
        operation = intent.getIntExtra(EXTRA_OPERATION, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // matchBean = (MatchBean[]) getIntent().getSerializableExtra("matchBean");

        gameTradeInApplication = (GameTradeInApplication) getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();

        // userId =  gameTradeInApplication.GetLoginUser().getUserId();
        // authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());

        userId = Integer.valueOf(QueryPreferences.getStoredUserIdQuery(getApplicationContext()));
        authorizedHeader = QueryPreferences.getStoredAuthorizedQuery(getApplicationContext());

        mGameTradeService = GameTradeApi.getClient(authorizedHeader).create(GameTradeService.class);

        setContentView(R.layout.activity_address);

        retrieveFromIntent(getIntent());

        Intent intentReceiver = getIntent();

        mMainProgress = (ProgressBar) findViewById(R.id.main_progress);

        listView = (ListView) findViewById(R.id.addressListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addressToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.findViewById(R.id.action_add);
        // addressOperationButton = (Button) findViewById(R.id.itemAddressOperationButton);

        GetAddress();

        /*SimpleAdapter adapter = new SimpleAdapter(this, getAddressData(addressBean), R.layout.item_address,
                new String[]{"itemAddressDetailAddressId", "itemAddressDetailReceiver", "itemAddressDetailPhone","itemAddressDetailAddress", "itemAddressDetailRegion"},
                new int[]{R.id.itemAddressDetailAddressId, R.id.itemAddressDetailReceiver, R.id.itemAddressDetailPhone, R.id.itemAddressDetailAddress, R.id.itemAddressDetailRegion}
        );




        listView.setAdapter(adapter);

        try {
            switch (operation) {
                case "match":
                    listView.setOnItemClickListener(onAddressItemClickListener);
                    break;
                case "browse":
                    listView.setOnItemClickListener(onAddressItemBrowseClickListener);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc){
            Log.d("error", exc.toString());
        }*/

    }


    @Override
    protected void onResume() {
        super.onResume();

        GetAddress();

        SimpleAdapter adapter = new SimpleAdapter(this, getAddressData(addressBean), R.layout.item_address,
                new String[]{"itemAddressDetailAddressId", "itemAddressDetailReceiver", "itemAddressDetailPhone","itemAddressDetailAddress", "itemAddressDetailRegion"},
                new int[]{R.id.itemAddressDetailAddressId, R.id.itemAddressDetailReceiver, R.id.itemAddressDetailPhone, R.id.itemAddressDetailAddress, R.id.itemAddressDetailRegion}
        );


        listView.setAdapter(adapter);

        try {
            switch (operation) {
                case OPERATION_MATCH:
                    listView.setOnItemClickListener(onAddressItemClickListener);
                    break;
                case OPERATION_BROWSE:
                    listView.setOnItemClickListener(onAddressItemBrowseClickListener);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc){
            Log.d("error", exc.toString());
        }

    }



    /*****************************************************************************************/
    /* Function for toolbar */

    protected void setList(){
        SimpleAdapter adapter = new SimpleAdapter(this, getAddressData(addressBean), R.layout.item_address,
                new String[]{"itemAddressDetailAddressId", "itemAddressDetailReceiver", "itemAddressDetailPhone","itemAddressDetailAddress", "itemAddressDetailRegion"},
                new int[]{R.id.itemAddressDetailAddressId, R.id.itemAddressDetailReceiver, R.id.itemAddressDetailPhone, R.id.itemAddressDetailAddress, R.id.itemAddressDetailRegion}
        );
        listView.setAdapter(adapter);
        try {
            switch (operation) {
                case  OPERATION_MATCH:
                    listView.setOnItemClickListener(onAddressItemClickListener);
                    break;
                case  OPERATION_BROWSE:
                    listView.setOnItemClickListener(onAddressItemBrowseClickListener);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc){
            Log.d("error", exc.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            getMenuInflater().inflate(R.menu.toolbar, menu);
            switch (operation) {
                case OPERATION_BROWSE:
                    menu.findItem(R.id.action_add).setVisible(true);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc){
            Log.d("",exc.toString());
        }
        return true;
    }


    /*****************************************************************************************/
    /* Function for json */


    private JSONObject formatMatchConfirmJSON(String gameId, String targetUserId, String addressId) {
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "gameId": gameId,
            //     "targetUserId": targetUserId
            //     "addressId": addressId
            // }
            root.put("gameId", gameId);
            root.put("targetUserId", targetUserId);
            root.put("addressId", addressId);
            return root;
        } catch (JSONException exc) {
            exc.printStackTrace();
        }
        return root;
    }


    /*****************************************************************************************/
    /* ListView item_game_tile click settings */



    private AdapterView.OnItemClickListener onAddressItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ListView listView = (ListView) parent;
            HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
            String addressIdStr ="";

            Long addressId;

            try{
                addressIdStr = String.valueOf(map.get("itemAddressDetailAddressId"));
                if(addressIdStr == "") return;
                addressId = Long.getLong(addressIdStr);

                listView.setClickable(false);
                listView.setOnItemClickListener(null);

                mMainProgress.setVisibility(View.VISIBLE);
                mGameTradeService.confirmMatch(userId.longValue(), myGameId,
                        new CreateOrderBean( targetGameId, targetUserId, addressId))
                        .enqueue(new Callback<TradeConfirmBean>() {
                            @Override
                            public void onResponse(Call<TradeConfirmBean> call, Response<TradeConfirmBean> response) {



                                mMainProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Order Successfully Created", Toast.LENGTH_SHORT)
                                        .show();

                                Intent intent = new Intent(AddressActivity.this, OrderActivity.class);
                                startActivity(intent);

                            }

                            @Override
                            public void onFailure(Call<TradeConfirmBean> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT)
                                        .show();

                                listView.setClickable(true);
                                listView.setOnItemClickListener(onAddressItemClickListener);

                                mMainProgress.setVisibility(View.GONE);

                            }
                        });
            }
            catch (Exception exc)
            {
                showDialog(exc.toString());
            }
        }
    };

    private AdapterView.OnItemClickListener onAddressItemBrowseClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
            String addressId ="";

            try{
                addressId = String.valueOf(map.get("itemAddressDetailAddressId"));
                //ConfirmMatch(gameId, targetUserId, addressId);
                Intent intent = new Intent();
                intent.putExtra("addressId", addressId);
                intent.putExtra("addressOperation", "modify");
                intent.setClass(AddressActivity.this, AddressOperationActivity.class);
                startActivity(intent);
            }
            catch (Exception exc)
            {
                showDialog(exc.toString());
            }
        }
    };



    /*****************************************************************************************/
    /* Toolbar settings */


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String message = "";
            Intent intent = new Intent();
            switch (menuItem.getItemId()){
                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
                case R.id.action_add:
                    intent.putExtra("addressOperation", "add");
                    intent.setClass(AddressActivity.this, AddressOperationActivity.class);
                    startActivity(intent);
            }
            if(!message.equals(""))
            {
                Toast.makeText(AddressActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };



    /*****************************************************************************************/
    /* Part for access to address detail task */


    private class AddressDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public Boolean finish = false;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConn;
            try {

                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("user")
                        .appendPath(String.valueOf(userId))
                        .appendPath("address");

                urlStr = serverUrl + builder.build().toString();
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
                addressBean = jsonProcessor.GetAddressListBean(status);
                switch (addressBean.length){
                    case 0:
                        canChoose = false;
                        status = "No address access";
                        break;
                    default:
                        canChoose = true;
                        status = "Available address";
                        break;
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            super.onProgressUpdate(progresses);
        }
        @Override
        protected void onPostExecute(String result) {
            if(!canChoose){
                if((status == null)== false) {
                    showDialog(status);
                }
                mHandler.sendEmptyMessage(0);
            }
            super.onPostExecute(result);
        }
    }

    public void GetAddress(){
        AddressDetailTask addressDetailTask = new AddressDetailTask();
        try {
            String test = addressDetailTask.execute().get();
            // addressDetailTask.execute();
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    setList();
                    break;
                default:
                    break;
            }
        }
    };

    /*****************************************************************************************/
    /* Part for confirm match task */



    /*****************************************************************************************/
    // For setting match list


    private List<Map<String, Object>> getAddressData(AddressBean[] addressBean){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        if(addressBean == null){
            showDialog("no address, please add one");
        }
        else{
            for (int i = 0; i < addressBean.length; i++) {
                map = new HashMap<String, Object>();
                map.put("itemAddressDetailAddressId", addressBean[i].getAddressId());
                map.put("itemAddressDetailReceiver", addressBean[i].getReceiver());
                map.put("itemAddressDetailPhone", addressBean[i].getPhone());
                map.put("itemAddressDetailAddress", addressBean[i].getAddress());
                map.put("itemAddressDetailRegion", addressBean[i].getRegion());
                list.add(map);
            }
        }
        return list;
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
}
