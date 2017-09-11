package com.example.ye.gametrade_in;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by ye on 2017/7/14.
 */

public class AddressOperationActivity extends AppCompatActivity{

    GameTradeInApplication gameTradeInApplication;
    String serverUrl, authorizedHeader;
    Integer userId;
    Intent intentReceiver = new Intent();
    String addressOperation;
    EditText receiverEdit, phoneEdit, addressEdit, regionEdit;
    String receiver, phone, address, region, addressId;
    Button addressOperationButton;
    Boolean canJmp = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            gameTradeInApplication = (GameTradeInApplication) getApplication();
            serverUrl = gameTradeInApplication.getServerUrl();
            //userId = gameTradeInApplication.GetLoginUser().getUserId();
            //authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());
            userId = Integer.valueOf(QueryPreferences.getStoredUserIdQuery(getApplicationContext()));
            authorizedHeader = QueryPreferences.getStoredAuthorizedQuery(getApplicationContext());

            setContentView(R.layout.activity_address_detail);
            receiverEdit = (EditText) findViewById(R.id.addressDetailReceiver);
            phoneEdit = (EditText) findViewById(R.id.addressDetailPhone);
            addressEdit = (EditText) findViewById(R.id.addressDetailAddress);
            regionEdit = (EditText) findViewById(R.id.addressDetailRegion);

            addressOperationButton = (Button) findViewById(R.id.addressOperationButton);

            addressOperationButton.setOnClickListener(onAddressOperationClickListener);

            intentReceiver = getIntent();
            addressOperation = intentReceiver.getStringExtra("addressOperation");
            addressId = intentReceiver.getStringExtra("addressId");


            Toolbar toolbar = (Toolbar) findViewById(R.id.addressDetailToolBar);
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

            switch(addressOperation) {
                case "add":
                    addressOperationButton.setText("add address");
                    break;
                case "modify":
                    addressOperationButton.setText("modify address");
                    new AddressOperationActivity.AddressDetailTask().execute();
                    break;
                default:
                    break;
            }

        }
        catch(Exception exc)
        {
            showDialog(exc.toString());
        }
    }


    /*****************************************************************************************/
    /* Function for toolbar */


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    /*****************************************************************************************/
    /* Button settings */


    private View.OnClickListener onAddressOperationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            receiver = receiverEdit.getText().toString();
            phone = phoneEdit.getText().toString();
            address = addressEdit.getText().toString();
            region = regionEdit.getText().toString();
            AddressOperation(receiver, phone, address, region);
        }
    };





    /*****************************************************************************************/
    /* Function for json */



    private JSONObject formatAddressJSON(String receiver, String phone, String address, String region) {
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "receiver": receiver,
            //     "phone": phone,
            //     "address": address,
            //     "region": region
            // }
            root.put("receiver", receiver);
            root.put("phone", phone);
            root.put("address", address);
            root.put("region", region);
            return root;
        } catch (JSONException exc) {
            exc.printStackTrace();
        }
        return root;
    }



    /*****************************************************************************************/
    /* Address operation functions */


    void AddressOperation(String receiver, String phone, String address, String region){
        JSONObject  jsonObject =formatAddressJSON(receiver, phone, address, region);
        new AddressOperationTask().execute(jsonObject);
    }




    /*****************************************************************************************/
    /* Part for access to address detail task */


    private class AddressDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public Boolean finish = false;
        public AddressBean addressBean;
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
                        .appendPath("address")
                        .appendPath(addressId);


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
                addressBean = jsonProcessor.GetAddressSingleBean(status);
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
            receiverEdit.setText(addressBean.getReceiver());
            phoneEdit.setText(addressBean.getPhone());
            addressEdit.setText(addressBean.getAddress());
            regionEdit.setText(addressBean.getRegion());
            super.onPostExecute(result);
        }
    }

    /*****************************************************************************************/
    /* Part for address operation task */

    private class AddressOperationTask extends AsyncTask<JSONObject, Integer, String> {
        private String status, urlStr;
        private JSONObject postJson;
        private int responseCode = -1;
        private String operationUrl;

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(JSONObject... params) {
            postJson = params[0];
            HttpURLConnection urlConn;
            Uri.Builder builder = new Uri.Builder();
            try {
                switch (addressOperation) {
                    case "add":
                        builder.appendPath("api")
                                .appendPath("user")
                                .appendPath(String.valueOf(userId))
                                .appendPath("address");
                        // operationUrl = "/address";
                        break;
                    case "modify":
                        builder.appendPath("api")
                                .appendPath("user")
                                .appendPath(String.valueOf(userId))
                                .appendPath("address")
                                .appendPath(addressId);
                        // operationUrl = "/address/" + addressId;
                        break;
                    default:
                        break;
                }
                // urlStr = serverUrl + "api/user/" + userId + operationUrl ;
                urlStr = serverUrl +builder.build().toString();
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);

                urlConn.setRequestProperty("Authorization", authorizedHeader);
                switch (addressOperation) {
                    case "add":
                        urlConn.setRequestMethod("POST");
                        break;
                    case "modify":
                        urlConn.setRequestMethod("PUT");
                        break;
                    default:
                        break;
                }

                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.connect();
                OutputStream out = urlConn.getOutputStream();
                out.write(postJson.toString().getBytes());
                out.flush();
                out.close();
                responseCode = urlConn.getResponseCode();
                if (responseCode == 200) {
                    status = "Operation successfully completed.";
                    canJmp = true;
                } else if (responseCode == 409) {
                    status = "Operation failed";
                } else if (responseCode == 404) {
                    status = "Operation failed";
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
            showDialog(status);
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
                if(canJmp){
//                    Intent intent = new Intent();
//                    intent.putExtra("operation","browse");
//                    intent.setClass(AddressOperationActivity.this, AddressActivity.class);
//                    startActivity(intent);
                    finish();
                }
                else{

                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


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
            }
            if(!message.equals(""))
            {
                Toast.makeText(AddressOperationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };






    /*****************************************************************************************/

}
