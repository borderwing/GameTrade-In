package com.example.ye.gametrade_in;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FragmentRegister extends Fragment{

    private EditText registerNameText, registerEmailText, registerPhoneText, registerPasswordText;
    private String registerName, registerEmail, registerPhone, registerPassword;
    Button registerButton;
    String serverUrl;
    Boolean jmpToLog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        GameTradeInApplication gameTradeInApplication = (GameTradeInApplication) getActivity().getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        registerNameText = (EditText) getView().findViewById(R.id.registerName);
        registerEmailText = (EditText) getView().findViewById(R.id.registerEmail);
        registerPhoneText = (EditText) getView().findViewById(R.id.registerPhNum);
        registerPasswordText = (EditText) getView().findViewById(R.id.registerPassword);

        registerButton = (Button) getView().findViewById(R.id.registerButton);
        registerButton.setOnClickListener(onRegisterListener);
    }


    /*****************************************************************************************/
    /* Helper Function */


    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                if( jmpToLog ){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class );
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*****************************************************************************************/
    /* Button settings */


    private View.OnClickListener onRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerName = registerNameText.getText().toString();
            registerEmail = registerEmailText.getText().toString();
            registerPhone = registerPhoneText.getText().toString();
            registerPassword = registerPasswordText.getText().toString();
            Register(registerName, registerEmail, registerPhone, registerPassword);
        }
    };



    /*****************************************************************************************/
    /* Function for json */



    private JSONObject formatJSON(String registerName, String registerEmail, String registerPhone, String registerPassword){
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "username": username,
            //     "email": email
            //     "phone": phone
            //     "password": password
            // }
            root.put("username", registerName);
            root.put("email", registerEmail);
            root.put("phone", registerPhone);
            root.put("password", registerPassword);
            return root;
        }
        catch(JSONException exc){
            exc.printStackTrace();
        }
        return root;
    }



    /*****************************************************************************************/
    /* Part for register */


    private void Register(String registerName, String registerEmail, String registerPhone, String registerPassword){
        final JSONObject postJson = formatJSON(registerName, registerEmail, registerPhone, registerPassword);
        new RegisterTask().execute(postJson);
    }


    private class RegisterTask extends AsyncTask<JSONObject, Integer, String> {
        private String status,urlStr;
        private JSONObject postJson;
        private int responseCode = -1;
        @Override
        protected  void onPreExecute(){
            status = "Username is available. Please login. ";
        }
        @Override
        protected  String doInBackground(JSONObject... params){
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("register")
                        .appendPath("");
                urlStr = serverUrl + builder.build().toString();
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
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
                responseCode = urlConn.getResponseCode();
                if(responseCode == 409){
                    status = "Username exists, please pick another username. ";
                }
                else if(responseCode == 201){
                    jmpToLog = true;
                    status = "Username is available. Please login. ";
                }
            }
            catch (Exception exc){
                exc.printStackTrace();
                status = "Disconnected: check connection status." ;
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
            showDialog(status);
            super.onPostExecute(result);
        }
    }



    /*****************************************************************************************/
}
