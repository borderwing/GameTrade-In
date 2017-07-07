package com.example.ye.gametrade_in;
import android.app.Fragment;
import android.content.DialogInterface;
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

    private EditText registerNameText, registerEmailText, registerPhoneText;
    private String registerName, registerEmail, registerPhone;
    Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        registerNameText = (EditText) getView().findViewById(R.id.registerName);
        registerEmailText = (EditText) getView().findViewById(R.id.registerEmail);
        registerPhoneText = (EditText) getView().findViewById(R.id.registerPhNum);
        registerButton = (Button) getView().findViewById(R.id.registerButton);
        registerButton.setOnClickListener(onRegisterListener);
    }

    private View.OnClickListener onRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // showDialog("gg");
            registerName = registerNameText.getText().toString();
            registerEmail = registerEmailText.getText().toString();
            registerPhone = registerPhoneText.getText().toString();
            Register(registerName, registerEmail, registerPhone);
        }
    };

    private void Register(String registerName, String registerEmail, String registerPhone){
        final JSONObject postJson = formatJSON(registerName, registerEmail, registerPhone);
        new RegisterTask().execute(postJson);
    }

    private class RegisterTask extends AsyncTask<JSONObject, Integer, String> {
        private String status,urlStr;
        private JSONObject postJson;
        private int responseCode = -1;

        @Override
        protected  void onPreExecute(){
            status = "Username is available. Please go back to main menu and login. ";
        }

        @Override
        protected  String doInBackground(JSONObject... params){
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                // register url
                urlStr = "http://192.168.1.27:8080/api/register/";
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
                responseCode = urlConn.getResponseCode();
                if(responseCode == 409){
                    status = "Username exists, please pick another username. ";
                }
                // status += "Connected: " + postJson.toString() + " " + responseCode;
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



    private JSONObject formatJSON(String registerName, String registerEmail, String registerPhone){
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "username": username,
            //     "email": email
            //     "phone": phone
            // }
            root.put("username", registerName);
            root.put("email", registerEmail);
            root.put("phone", registerPhone);
            return root;
        }
        catch(JSONException exc){
            exc.printStackTrace();
        }
        return root;
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
}
