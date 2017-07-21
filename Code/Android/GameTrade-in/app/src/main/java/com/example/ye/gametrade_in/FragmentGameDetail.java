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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameReleaseJson;
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
import java.util.ArrayList;
import java.util.List;

public class FragmentGameDetail extends Fragment{

    TextView gameTitleView, gameTextView, gameCategoryPlatform, gameCategoryLanguage, gameCategoryGenre, gameCreditView, gamePopularity;
    Spinner platformSpinner, regionSpinner;
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
    ArrayAdapter <String> platformAdapter;
    ArrayAdapter <String> regionAdapter;
    List<String> platformList;
    List<String> regionList;
    List<Integer> platformIdList;
    List<Integer> regionIdList;
    Integer selectedPlatformId, selectedRegionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        platformList = new ArrayList<String>();
        regionList = new ArrayList<String>();
        platformIdList = new ArrayList<Integer>();
        regionIdList = new ArrayList<Integer>();

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

        GameDetailTask gameDetailTask = new GameDetailTask();

        gameTitleView = (TextView) getView().findViewById(R.id.gameTitle);
        gameTextView = (TextView) getView().findViewById(R.id.gameText);
        gameCreditView = (TextView) getView().findViewById(R.id.creditAmount);
        addToListEdit = (EditText) getView().findViewById(R.id.addToListPoints);
        gamePopularity = (TextView) getView().findViewById(R.id.popularityText);

        gameTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        try{
            String test = gameDetailTask.execute(gameDetailId).get();
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }

        try {
            ImageView gameImageView = (ImageView) getView().findViewById(R.id.gameImage);
            gameImageView.setImageBitmap(bitmap);
        }
        catch (Exception exc){
            showDialog(exc.toString());
        }



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

                platformSpinner = (Spinner) getView().findViewById(R.id.categoryPlatformSpinner);
                platformSpinner.setPrompt("Choose platform");
                platformAdapter = new ArrayAdapter<String>(this.getActivity() , R.layout.support_simple_spinner_dropdown_item, platformList);
                platformSpinner.setAdapter(platformAdapter);
                platformSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Toast.makeText(getActivity(),"Your choice："+platformList.get(position)+platformIdList.get(position), Toast.LENGTH_SHORT).show();
                        selectedPlatformId = platformIdList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedPlatformId = platformIdList.get(0);
                    }
                });
                regionSpinner = (Spinner) getView().findViewById(R.id.categoryRegionSpinner);
                regionSpinner.setPrompt("Choose region");
                regionAdapter = new ArrayAdapter<String>(this.getActivity() , R.layout.support_simple_spinner_dropdown_item, regionList);
                regionSpinner.setAdapter(regionAdapter);
                regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Toast.makeText(getActivity(),"Your choice："+regionList.get(position)+regionIdList.get(position), Toast.LENGTH_SHORT).show();
                        selectedRegionId = regionIdList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedRegionId = regionIdList.get(0);
                    }
                });
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
                AddToList(Integer.valueOf(gameDetailId), selectedPlatformId, selectedRegionId, addToListPoints);
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
                AddToOfferList(Integer.valueOf(gameDetailId), selectedPlatformId, selectedRegionId, addToListPoints);
            }
        }
    };


    /*****************************************************************************************/
    /* Function for json */


    private JSONObject formatJSON(Integer igdbId, Integer platformId, Integer regionId, Integer wishPoints) {
        final JSONObject root = new JSONObject();
        try {
            // JSON
            // {
            //     "gameId": gameId,
            //     "points": wishPoints
            // }
            root.put("igdbId", igdbId);
            root.put("platformId", platformId);
            root.put("regionId", regionId);
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
                        credits = "0";
                        break;
                    default:
                        break;
                }

                List<GameReleaseJson> gameReleaseJsons = gameDetail.getReleases();
                for (int p = 0; p < gameReleaseJsons.size(); p++) {
                    platformList.add(gameReleaseJsons.get(p).getPlatform());
                    platformIdList.add(gameReleaseJsons.get(p).getPlatformId());
                    regionList.add(gameReleaseJsons.get(p).getRegion());
                    regionIdList.add(gameReleaseJsons.get(p).getRegionId());
                }
                setGameDetail(gameDetail.getTitle(), String.valueOf(((int) gameDetail.getPopularity())), gameDetail.getSummary());
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
            try {

            }
            catch (Exception exc){
                showDialog(exc.toString());
            }
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
                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("user")
                        .appendPath(userId)
                        .appendPath("wishlist");
                urlStr = serverUrl +builder.build().toString();

                // urlStr = serverUrl + "api/user/" + userId + "/wishlist/";

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
                }else if(responseCode == 401){
                    status = "Please log in";
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


    private void AddToList(Integer gameId, Integer platformId, Integer regionId, Integer wishPoints) {
        final JSONObject postJson = formatJSON(gameId, platformId, regionId, wishPoints);
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
                Uri.Builder builder = new Uri.Builder();
                builder.appendPath("api")
                        .appendPath("user")
                        .appendPath(userId)
                        .appendPath("offerlist");
                urlStr = serverUrl +builder.build().toString();
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


    private void AddToOfferList(Integer gameId, Integer platformId, Integer regionId,Integer wishPoints) {
        final JSONObject postJson = formatJSON(gameId, platformId, regionId, wishPoints);
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
