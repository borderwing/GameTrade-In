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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private EditText userName, userPassword;
    public Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationIcon(R.drawable.nav);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        userName = (EditText) this.findViewById(R.id.loginUserName);
        userPassword = (EditText) this.findViewById(R.id.loginPassword);

        login = (Button) this.findViewById(R.id.loginButton);
        login.setOnClickListener(onLoginClickListener);

    }

    private void showDialog(String msg, final Boolean canJump){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                if(canJump) {
                    BackToMainWithId(userId);
                }
                else if(!canJump){
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                    intent.setClass(LoginActivity.this, MyListActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case R.id.action_publish:
                    intent = new Intent();
                    intent.setClass(LoginActivity.this, PublishActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case R.id.action_gameDetail:
                    intent = new Intent();
                    intent.setClass(LoginActivity.this, GameDetailActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case R.id.action_register:
                    intent = new Intent();
                    intent.setClass(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case R.id.action_orderDetail:
                    intent = new Intent();
                    intent.setClass(LoginActivity.this, OrderDetailActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
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
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                String nameString = userName.getText().toString();
                String password = userPassword.getText().toString();
                login(nameString, password);

        }
    };


    private JSONObject formatJSON(String username, String password){
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "username": username,
            //     "password": password
            // }
            root.put("username", username);
            root.put("password", password);
            return root;//.toString();
        }
        catch(JSONException exc){
            exc.printStackTrace();
        }
        return root;//.toString();
    }

    private void login(String username, String password){
        String urlStr = "http://192.168.1.27:8080/api/login";
        final JSONObject postJson = formatJSON(username, password);
        new LoginTask().execute(postJson);
    }

    public void BackToMainWithId(Integer userId){
        Intent intent = new Intent();
        intent.putExtra("userId", userId);
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private class LoginTask extends AsyncTask<JSONObject, Integer, String>{
        private String status,urlStr;
        private JSONObject postJson;
        public UserBean user;
        public boolean canJump;
        private int responseCode = -1;

        @Override
        protected  void onPreExecute(){
            // showDialog("start");
        }

        @Override
        protected  String doInBackground(JSONObject... params){
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/login/";
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                // start connection
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type","application/json");
                urlConn.connect();

                OutputStream out = urlConn.getOutputStream();

                out.write(postJson.toString().getBytes());
                out.flush();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                JSONProcessor jsonProcessor = new JSONProcessor();
                user = jsonProcessor.GetUserBean(reader.readLine());
                out.close();
                // upload json
                userId = user.userId;
                responseCode = urlConn.getResponseCode();

                if(responseCode == 200){
                    status = "Welcome back: "+ userId;
                }
                else if(responseCode == 404){
                    status = "Please input right username and password";
                }
                canJump = true;
                //status="connected: " + user.toString() + postJson.toString()+ " " + responseCode;
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Please input right username and password";
                canJump = false;
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
            //showDialog("finish");
            showDialog(status, canJump);
            /*BackToMainWithId(userId);*/
            //showDialog(postJson);
            super.onPostExecute(result);
        }
    }
}
