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
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentGameDetail extends Fragment{

    TextView gameTitleView, gameTextView;
    String gameTitle, gameText;
    Integer points;
    ImageButton addToList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        return inflater.inflate(R.layout.fragment_gamedetail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        gameTitleView = (TextView) getView().findViewById(R.id.gameTitle);
        gameTextView = (TextView) getView().findViewById(R.id.gameText);
        addToList = (ImageButton) getView().findViewById(R.id.addToListButton);
        addToList.setOnClickListener(onAddToListListener);
    }

    private View.OnClickListener onAddToListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // showDialog("gg");
            new GameDetailTask().execute(1);
        }
    };

    private class GameDetailTask extends AsyncTask<Integer, Integer, String> {
        private String status, urlStr, getJson;
        private int responseCode = -1;

        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(Integer... params){
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/game/"+params[0];
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                status="connected: " +reader.readLine()+ " " + responseCode;
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
            // showDialog("......");
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
