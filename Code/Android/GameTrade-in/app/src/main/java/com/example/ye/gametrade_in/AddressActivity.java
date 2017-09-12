package com.example.ye.gametrade_in;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.AddressBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ye on 2017/7/11.
 */

public class AddressActivity extends AppCompatActivity {

    GameTradeInApplication gameTradeInApplication;
    String gameDetailId;
    private String gameId, targetUserId, operation;
    String serverUrl;
    Integer userId;
    boolean canChoose;
    private AddressBean[] addressBean;
    private ListView listView;
    String authorizedHeader;
    Button addressOperationButton;



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
        setContentView(R.layout.activity_address);

        Intent intentReceiver = getIntent();
        gameId = intentReceiver.getStringExtra("gameId");
        targetUserId = intentReceiver.getStringExtra("targetUserId");
        gameDetailId = intentReceiver.getStringExtra("gameDetailId");
        operation = intentReceiver.getStringExtra("operation");
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
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            getMenuInflater().inflate(R.menu.toolbar, menu);
            switch (operation) {
                case "browse":
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
            ListView listView = (ListView) parent;
            HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
            String addressId ="";

            try{
                addressId = String.valueOf(map.get("itemAddressDetailAddressId"));
                ConfirmMatch(gameId, targetUserId, addressId);
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



    private class ConfirmMatchTask extends AsyncTask<JSONObject, Integer, String> {
        private String status, urlStr;
        private JSONObject postJson;
        private int responseCode = -1;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(JSONObject... params) {
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("user")
                        .appendPath(String.valueOf(userId))
                        .appendPath("wishlist")
                        .appendPath(gameDetailId)
                        .appendPath("match")
                        .appendPath("confirm");
                urlStr = serverUrl + builder.build().toString();
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Authorization", authorizedHeader);
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.connect();
                OutputStream out = urlConn.getOutputStream();
                out.write(postJson.toString().getBytes());
                out.flush();
                out.close();
                responseCode = urlConn.getResponseCode();
                if (responseCode == 200) {
                    status = "Match confirmed";
                } else if (responseCode == 409) {
                    status = "Failed, please refresh";
                } else if (responseCode == 404) {
                    status = "Failed, please refresh";
                } else {
                    status = "Failed, please refresh";
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                status = "Disconnected: " + responseCode;
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            super.onProgressUpdate(progresses);
        }
        @Override
        protected void onPostExecute(String result) {
            if ((status == null) == false) {
                showDialog(status);
            }
            else{
            }
            super.onPostExecute(result);
        }
    }


    private void ConfirmMatch(String gameId, String targetUserId, String addressId) {
        final JSONObject postJson = formatMatchConfirmJSON(gameId, targetUserId, addressId);
        new AddressActivity.ConfirmMatchTask().execute(postJson);
    }




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
