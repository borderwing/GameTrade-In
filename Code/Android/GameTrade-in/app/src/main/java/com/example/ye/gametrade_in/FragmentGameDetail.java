package com.example.ye.gametrade_in;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentGameDetail extends Fragment{

    TextView gameTitleView, gameTextView, gameCategoryPlatform, gameCategoryLanguage, gameCategoryGenre, gameCreditView;
    String gameTitle, gameText;
    Integer points;
    ImageButton addToList;
    String gameDetailId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        gameDetailId=getArguments().getString("gameId");
        return inflater.inflate(R.layout.fragment_gamedetail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        gameTitleView = (TextView) getView().findViewById(R.id.gameTitle);
        gameTextView = (TextView) getView().findViewById(R.id.gameText);
        gameCreditView = (TextView) getView().findViewById(R.id.creditAmount);

        gameCategoryPlatform = (TextView) getView().findViewById(R.id.categoryPlatformName);
        gameCategoryLanguage = (TextView) getView().findViewById(R.id.categoryLanguageName);
        gameCategoryGenre = (TextView) getView().findViewById(R.id.categoryGenreName);

        addToList = (ImageButton) getView().findViewById(R.id.addToListButton);
        addToList.setOnClickListener(onAddToListListener);

        new GameDetailTask().execute(gameDetailId);
    }

    private View.OnClickListener onAddToListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //new GameDetailTask().execute(1);
            /*//
            GameDetailTask gameDetailTask = new GameDetailTask();
            gameDetailTask.execute(1);*/
        }
    };

    private void setGameDetail(String title, String platform, String language, String genre, String credits){
        gameTitleView.setText(title);
        gameCategoryPlatform.setText(platform);
        gameCategoryLanguage.setText(language);
        gameCategoryGenre.setText(genre);
        gameCreditView.setText(credits);
    }

    private class GameDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public GameBean game;
        public Boolean finish = false;
        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(String... params){
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
                JSONProcessor jsonProcessor = new JSONProcessor();
                game = jsonProcessor.GetGameBean(reader.readLine());
                finish = true;
                // status = "connected: "  + game.platform + " " + responseCode;
            }
            catch (Exception exc){
                exc.printStackTrace();
                // status = "Disconnected: " + responseCode;
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
            setGameDetail(game.title,game.platform, game.language, game.genre, String.valueOf(game.evaluatePoint));
            // showDialog(status);
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
