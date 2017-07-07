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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentGameDetail extends Fragment{

    TextView gameTitleView, gameTextView, gameCategoryPlatform, gameCategoryLanguage, gameCategoryGenre, gameCreditView;
    EditText addToListEdit;
    String gameTitle, gameText;
    Integer addToListPoints;
    ImageButton addToWishList;
    Button addToOfferList;
    String gameDetailId;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        Bundle bundle = getArguments();
        gameDetailId = bundle.getString("gameId");
        userId = bundle.getString("userId");
        return inflater.inflate(R.layout.fragment_gamedetail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        gameTitleView = (TextView) getView().findViewById(R.id.gameTitle);
        gameTextView = (TextView) getView().findViewById(R.id.gameText);
        gameCreditView = (TextView) getView().findViewById(R.id.creditAmount);
        addToListEdit = (EditText) getView().findViewById(R.id.addToListPoints);

        gameCategoryPlatform = (TextView) getView().findViewById(R.id.categoryPlatformName);
        gameCategoryLanguage = (TextView) getView().findViewById(R.id.categoryLanguageName);
        gameCategoryGenre = (TextView) getView().findViewById(R.id.categoryGenreName);

        addToWishList = (ImageButton) getView().findViewById(R.id.addToListButton);
        addToWishList.setOnClickListener(onAddToWishListListener);

        addToOfferList = (Button) getView().findViewById(R.id.addToWishListButton);
        addToOfferList.setOnClickListener(onAddToOfferListListener);

        new GameDetailTask().execute(gameDetailId);
    }

    private View.OnClickListener onAddToWishListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean canAddToWish ;
            try{
                addToListPoints = Integer.valueOf(addToListEdit.getText().toString());
                canAddToWish = true;
            }
            catch (Exception ece){
                showDialog("Wrong input");
                canAddToWish = false;
            }
            if (canAddToWish ) {
                AddToList(Integer.valueOf(gameDetailId), addToListPoints);
            }
        }
    };

    private View.OnClickListener onAddToOfferListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean canAddToOffer ;
            try{
                addToListPoints = Integer.valueOf(addToListEdit.getText().toString());
                canAddToOffer = true;
            }
            catch (Exception ece){
                showDialog("Wrong input");
                canAddToOffer = false;
            }
            if (canAddToOffer ) {
                AddToOfferList(Integer.valueOf(gameDetailId), addToListPoints);
            }
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
                urlStr = "http://192.168.1.27:8080/api/game/"+gameDetailId;
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

            }
            catch (Exception exc){
                exc.printStackTrace();

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
            setGameDetail(game.title,game.platform, game.language, game.genre, String.valueOf(game.evaluatePoint));
            super.onPostExecute(result);
        }
    }


    private JSONObject formatJSON(Integer gameId, Integer wishPoints){
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "gameId": gameId,
            //     "points": wishPoints
            // }
            root.put("gameId", gameId);
            root.put("points", wishPoints);
            return root;
        }
        catch(JSONException exc){
            exc.printStackTrace();
        }
        return root;
    }


    private void AddToList(Integer gameId, Integer wishPoints){
        final JSONObject postJson = formatJSON(gameId, wishPoints);
        new FragmentGameDetail.AddToListTask().execute(postJson);
    }

    private void AddToOfferList(Integer gameId, Integer wishPoints){
        final JSONObject postJson = formatJSON(gameId, wishPoints);
        new FragmentGameDetail.AddToOfferListTask().execute(postJson);
    }

    private class AddToListTask extends AsyncTask<JSONObject, Integer, String> {
        private String status,urlStr;
        private JSONObject postJson;
        private int responseCode = -1;

        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(JSONObject... params){
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/user/" + userId + "/wishlist/";
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
                if(responseCode == 200){
                    status = "Game has been successfully added to your wish list";
                }
                else if(responseCode == 409){
                    status = "Your wish list includes this game";
                }
                else if(responseCode == 404){
                    status = "Connection problem, check connect and login";
                }
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


    private class AddToOfferListTask extends AsyncTask<JSONObject, Integer, String> {
        private String status,urlStr;
        private JSONObject postJson;
        private int responseCode = -1;

        @Override
        protected  void onPreExecute(){
        }

        @Override
        protected  String doInBackground(JSONObject... params){
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                urlStr = "http://192.168.1.27:8080/api/user/" + userId + "/offer/";
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
                if(responseCode == 200){
                    status = "Game has been successfully added to your offer list";
                }
                else if(responseCode == 409){
                    status = "Your offer list includes this game";
                }
                else if(responseCode == 404){
                    status = "Connection problem, check connect and login";
                }
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
