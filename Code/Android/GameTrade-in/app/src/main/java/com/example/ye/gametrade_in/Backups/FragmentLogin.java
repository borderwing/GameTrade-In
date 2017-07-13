package com.example.ye.gametrade_in.Backups;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ye.gametrade_in.GameTradeInApplication;
import com.example.ye.gametrade_in.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentLogin extends Fragment{

    String serverUrl;
    private Button login;
    private EditText userName, userPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        GameTradeInApplication gameTradeInApplication = (GameTradeInApplication) getActivity().getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        userName = (EditText) getView().findViewById(R.id.loginUserName);
        userPassword = (EditText) getView().findViewById(R.id.loginPassword);

        userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        login = (Button) getView().findViewById(R.id.loginButton);
        login.setOnClickListener(onLoginClickListener);
    }


    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

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
        /*String urlStr = "http://192.168.1.27:8080/api/login";*/
        final JSONObject postJson = formatJSON(username, password);
        new FragmentLogin.LoginTask().execute(postJson);
    }

    private class LoginTask extends AsyncTask<JSONObject, Integer, String> {
        private String status,urlStr;
        private JSONObject postJson;
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
                urlStr = serverUrl + "api/login/";
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
                out.close();
                // upload json

                responseCode = urlConn.getResponseCode();
                status="connected: " +postJson.toString()+ " " + responseCode;
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Disconnected: " + responseCode;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses)
        {
            super.onProgressUpdate(progresses);
            showDialog("......");
        }

        @Override
        protected  void onPostExecute(String result)
        {
            //showDialog("finish");
            showDialog(status);
            //showDialog(postJson);
            super.onPostExecute(result);
        }
    }

}