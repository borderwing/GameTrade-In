package com.example.ye.gametrade_in;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.MatchBean;
import com.example.ye.gametrade_in.Bean.MyListBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentGameDetail extends Fragment{

    TextView gameTitleView, gameTextView, gameCategoryPlatform, gameCategoryLanguage, gameCategoryGenre, gameCreditView, gamePopularity;
    EditText addToListEdit;
    String gameTitle, gameText, operation, credits;
    Integer addToListPoints;
    Boolean toMatch = false;
    Button addToOfferList, matchButton, addToWishList, modifyButton;
    String gameDetailId, userId;
    MatchBean[] matchBean;
    String serverUrl;
    String authorizedHeader;
    public GameTradeInApplication gameTradeInApplication;
    BitmapBean bitmapBean;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        gameTradeInApplication = (GameTradeInApplication) getActivity().getApplication();
        authorizedHeader = gameTradeInApplication.GetAuthorizedHeader(gameTradeInApplication.GetUserAuthenticationBean());

        Bundle bundle = getArguments();

        gameDetailId = bundle.getString("igdbId");

        userId = bundle.getString("userId");
        operation = bundle.getString("operation");
        try {
            // bitmapBean = (BitmapBean) bundle.get("bitmapBean");
            bitmap = bundle.getParcelable("bitmap");
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }

        GameTradeInApplication gameTradeInApplication = (GameTradeInApplication) getActivity().getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        return inflater.inflate(R.layout.fragment_gamedetail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ImageView gameImageView = (ImageView) getView().findViewById(R.id.gameImage);
            gameImageView.setImageBitmap(bitmap);
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }
        gameTitleView = (TextView) getView().findViewById(R.id.gameTitle);
        gameTextView = (TextView) getView().findViewById(R.id.gameText);
        gameCreditView = (TextView) getView().findViewById(R.id.creditAmount);
        addToListEdit = (EditText) getView().findViewById(R.id.addToListPoints);
        gamePopularity = (TextView) getView().findViewById(R.id.popularityText);

        gameTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        /*gameCategoryPlatform = (TextView) getView().findViewById(R.id.categoryPlatformName);
        gameCategoryLanguage = (TextView) getView().findViewById(R.id.categoryLanguageName);
        gameCategoryGenre = (TextView) getView().findViewById(R.id.categoryGenreName);*/

        addToWishList = (Button) getView().findViewById(R.id.addToListButton);
        addToWishList.setOnClickListener(onAddToWishListListener);

        addToOfferList = (Button) getView().findViewById(R.id.addToWishListButton);
        addToOfferList.setOnClickListener(onAddToOfferListListener);

        matchButton = (Button) getView().findViewById(R.id.matchButton);
        matchButton.setOnClickListener(onMatchClickListener);

        modifyButton = (Button) getView().findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(onModifyClickListener);

        switch (operation) {
            case "browse":
                matchButton.setVisibility(View.GONE);
                modifyButton.setVisibility(View.GONE);
                break;
            case "wishList":
                addToWishList.setVisibility(View.GONE);
                modifyButton.setVisibility(View.VISIBLE);
                matchButton.setVisibility(View.VISIBLE);
                break;
            case "offerList":
                modifyButton.setVisibility(View.VISIBLE);
                addToOfferList.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        new GameDetailTask().execute(gameDetailId);
    }



    /*****************************************************************************************/
    /* Helper Function */


    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(toMatch){
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("matchBean", matchBean);
                    bundle.putSerializable("gameDetailId", gameDetailId);
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), MatchActivity.class);
                    try{
                        startActivity(intent);
                    }
                    catch (Exception exc){
                        showDialog(exc.toString());
                    }
                }
                else if(!toMatch){
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    /*****************************************************************************************/
    /* Button Listener settings */


    private View.OnClickListener onMatchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Match();
        }
    };


    private View.OnClickListener onModifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                addToListPoints = Integer.valueOf(addToListEdit.getText().toString());
                Modify(addToListPoints);
            } catch (Exception exc) {
                showDialog("Wrong input");
            }
        }
    };


    private View.OnClickListener onAddToWishListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean canAddToWish;
            try {
                addToListPoints = Integer.valueOf(addToListEdit.getText().toString());
                canAddToWish = true;
            } catch (Exception ece) {
                showDialog("Wrong input");
                canAddToWish = false;
            }
            if (canAddToWish) {
                AddToList(Integer.valueOf(gameDetailId), addToListPoints);
            }
        }
    };


    private View.OnClickListener onAddToOfferListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean canAddToOffer;
            try {
                addToListPoints = Integer.valueOf(addToListEdit.getText().toString());
                canAddToOffer = true;
            } catch (Exception ece) {
                showDialog("Wrong input");
                canAddToOffer = false;
            }
            if (canAddToOffer) {
                AddToOfferList(Integer.valueOf(gameDetailId), addToListPoints);
            }
        }
    };


    /*****************************************************************************************/
    /* Function for json */


    private JSONObject formatJSON(Integer gameId, Integer wishPoints) {
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
        } catch (JSONException exc) {
            exc.printStackTrace();
        }
        return root;
    }


    private JSONObject formatModifyJSON(Integer wishPoints) {
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "points": wishPoints
            // }
            root.put("points", wishPoints);
            return root;
        } catch (JSONException exc) {
            exc.printStackTrace();
        }
        return root;
    }


    /*****************************************************************************************/
    /*  Part for game detail task  */


    private class GameDetailTask extends AsyncTask<String, Integer, String> {
        private String status, urlStr;
        private int responseCode = -1;
        public GameDetailBean gameDetail;
        public MyListBean myList;
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
                        .appendPath("game")
                        .appendPath("");

                urlStr = serverUrl + builder.build().toString()+ gameDetailId;

                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                // urlConn.setRequestProperty("Authorization", authorizedHeader);
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                InputStream in = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                responseCode = urlConn.getResponseCode();
                JSONProcessor jsonProcessor = new JSONProcessor();

                gameDetail = jsonProcessor.GetGameDetailSingleBean(reader.readLine());


                switch (operation) {
                    case "wishList":
                        urlStr = serverUrl + "api/user/" + userId + "/wishlist/" + gameDetailId;
                        url = new URL(urlStr);
                        urlConn = (HttpURLConnection) url.openConnection();

                        urlConn.setRequestProperty("Authorization", authorizedHeader);

                        urlConn.setRequestMethod("GET");
                        urlConn.connect();
                        in = urlConn.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        responseCode = urlConn.getResponseCode();
                        jsonProcessor = new JSONProcessor();
                        myList = jsonProcessor.GetMyListSingleBean(reader.readLine());
                        credits = String.valueOf(myList.getPoints());
                        break;
                    case "offerList":
                        urlStr = serverUrl+"api/user/" + userId + "/offerlist/" + gameDetailId;
                        url = new URL(urlStr);
                        urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setRequestProperty("Authorization", authorizedHeader);
                        urlConn.setRequestMethod("GET");
                        urlConn.connect();
                        in = urlConn.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        responseCode = urlConn.getResponseCode();
                        jsonProcessor = new JSONProcessor();
                        myList = jsonProcessor.GetMyListSingleBean(reader.readLine());
                        credits = String.valueOf(myList.getPoints());
                        break;
                    case "browse":
                        // credits = String.valueOf(game.evaluatePoint);
                        credits = "0";
                        break;
                    default:
                        break;
                }
                finish = true;
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
            setGameDetail(gameDetail.getTitle(),String.valueOf(((int)gameDetail.getPopularity())), gameDetail.getSummary());

            // setGameDetail(game.title, game.platform, game.language, game.genre, credits);

            super.onPostExecute(result);
        }
    }


    private void setGameDetail(String title, String popularity, String text/*String language, String genre, String credits*/) {
        gameTitleView.setText(title);
        gamePopularity.setText(popularity);
        gameTextView.setText(text);
        //gameCategoryPlatform.setText(platform);
        //gameCategoryLanguage.setText(language);
        //gameCategoryGenre.setText(genre);
        //gameCreditView.setText(credits);
    }



    /*****************************************************************************************/
    /* Part for wish list task */


    private class AddToListTask extends AsyncTask<JSONObject, Integer, String> {
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
                urlStr = serverUrl + "api/user/" + userId + "/wishlist/";
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
                    status = "Game has been successfully added to your wish list";
                } else if (responseCode == 409) {
                    status = "Your wish list includes this game";
                } else if (responseCode == 404) {
                    status = "Connection problem, check connect and login";
                }
                else{
                    status = "Connection problem, check connect and login";
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


    private void AddToList(Integer gameId, Integer wishPoints) {
        final JSONObject postJson = formatJSON(gameId, wishPoints);
        new FragmentGameDetail.AddToListTask().execute(postJson);
    }


    /*****************************************************************************************/
    /* Part for offer list task */

    private class AddToOfferListTask extends AsyncTask<JSONObject, Integer, String> {
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
                urlStr = serverUrl + "api/user/" + userId + "/offerlist/";
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();

                // start connection
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
                // upload json

                responseCode = urlConn.getResponseCode();
                if (responseCode == 200) {
                    status = "Game has been successfully added to your offer list";
                } else if (responseCode == 409) {
                    status = "Your offer list includes this game";
                } else if (responseCode == 404) {
                    status = "Connection problem, check connect and login";
                }
                else{
                    status = "Connection problem, check connect and login";
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


    private void AddToOfferList(Integer gameId, Integer wishPoints) {
        final JSONObject postJson = formatJSON(gameId, wishPoints);
        new FragmentGameDetail.AddToOfferListTask().execute(postJson);
    }


    /*****************************************************************************************/
    /* Part for modify task */

    private class ModifyTask extends AsyncTask<JSONObject, Integer, String> {
        private String status, urlStr;
        private JSONObject postJson;
        private int responseCode = -1;
        private String modifyUrl;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(JSONObject... params) {
            postJson = params[0];
            HttpURLConnection urlConn;
            try {
                switch (operation) {
                    case "browse":
                        break;
                    case "wishList":
                        modifyUrl = "/wishlist/";
                        break;
                    case "offerList":
                        modifyUrl = "/offerlist/";
                        break;
                    default:
                        break;
                }
                urlStr = serverUrl + "api/user/" + userId + modifyUrl + gameDetailId + "/modify";
                URL url = new URL(urlStr);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);

                urlConn.setRequestProperty("Authorization", authorizedHeader);

                urlConn.setRequestMethod("PUT");
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.connect();
                OutputStream out = urlConn.getOutputStream();
                out.write(postJson.toString().getBytes());
                out.flush();
                out.close();
                responseCode = urlConn.getResponseCode();
                if (responseCode == 200) {
                    status = "Game has been successfully modified";
                    credits = String.valueOf(addToListPoints);
                } else if (responseCode == 409) {
                    status = "Modify failed";
                } else if (responseCode == 404) {
                    status = "Modify failed";
                }
                else{
                    status = "Connection problem, check connect and login";
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
            UIModify();
            showDialog(status);
            super.onPostExecute(result);
        }
    }



    private void Modify(Integer wishPoints) {
        final JSONObject postJson = formatModifyJSON(wishPoints);
        new FragmentGameDetail.ModifyTask().execute(postJson);
    }



    private void UIModify() {
        gameCreditView.setText(credits);
    }



    /*****************************************************************************************/
    /* Part for match task */



    private class MatchCheckTask extends AsyncTask<String, Integer, String> {
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
                urlStr = serverUrl + "api/user/" + userId + "/wishlist/" + gameDetailId + "/match";
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
                matchBean = jsonProcessor.GetMatchBean(status);
                switch (matchBean.length){
                    case 0:
                        toMatch = false;
                        status = "Match not found";
                        break;
                    default:
                        toMatch = true;
                        status = "Match found";
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
            showDialog(status);
            super.onPostExecute(result);
        }
    }

    public void Match(){
        new FragmentGameDetail.MatchCheckTask().execute();
    }


    /*****************************************************************************************/

}
