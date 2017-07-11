package com.example.ye.gametrade_in;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private String nameString, password;
    private EditText userName, userPassword;
    public Integer userId;
    public CheckBox loginPasswordCheckBox;

    public GameTradeInApplication gameTradeInApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) this.findViewById(R.id.loginUserName);
        userPassword = (EditText) this.findViewById(R.id.loginPassword);
        loginPasswordCheckBox = (CheckBox) this.findViewById(R.id.loginPasswordCheckBox);
        loginPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

        login = (Button) this.findViewById(R.id.loginButton);
        login.setOnClickListener(onLoginClickListener);
        gameTradeInApplication = (GameTradeInApplication) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolBar);
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

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            // LoginActivity.this.finish();
        }
    };

    private void showDialog(String msg, final Boolean canJump){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                if(canJump) {
                    BackToMain();   // If username and password is valid, just go back to main
                }
                else if(!canJump){
                                    // If username and password is invalid, nothing change
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

    // Helper menu
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
                    intent.setClass(LoginActivity.this, PublishActivity.class);
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

    // Click login button
    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                nameString = userName.getText().toString();
                password = userPassword.getText().toString();
                login(nameString, password);
        }
    };

    // Format String to JSON
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
            return root;
        }
        catch(JSONException exc){
            exc.printStackTrace();
        }
        return root;
    }

    // Login function
    private void login(String username, String password){
        final JSONObject postJson = formatJSON(username, password);
        new LoginTask().execute(postJson);
    }

    // Go back to main menu with userId
    public void BackToMainWithId(Integer userId){
        Intent intent = new Intent();
        intent.putExtra("userId", userId);
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    // Simply go bake to main menu
    public void BackToMain(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    // For login Internet connection
    private class LoginTask extends AsyncTask<JSONObject, Integer, String>{
        private String status,urlStr;
        private JSONObject postJson;
        public UserBean userBean;
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

                // Translate JSON to Bean
                userBean = jsonProcessor.GetUserBean(reader.readLine());
                out.close();

                // Set login user bean
                gameTradeInApplication.SetUserLogin(userBean);
                userId = gameTradeInApplication.GetLoginUser().getUserId();
                responseCode = urlConn.getResponseCode();

                if(responseCode == 200){
                    status = "Welcome back: " + nameString;
                }
                else if(responseCode == 404){
                    status = "Please input right username and password";
                }
                canJump = true;
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
            showDialog(status, canJump);
            super.onPostExecute(result);
        }
    }
}
