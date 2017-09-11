package com.example.ye.gametrade_in;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.BitmapBean;
import com.example.ye.gametrade_in.Bean.GameDetailBean;
import com.example.ye.gametrade_in.Bean.GameReleaseJson;
import com.example.ye.gametrade_in.Bean.MatchBean;
import com.example.ye.gametrade_in.Bean.MyListBean;
import com.example.ye.gametrade_in.api.GameTradeApi;
import com.example.ye.gametrade_in.api.GameTradeService;

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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentGameDetail extends Fragment{

    public static final String ARG_IGDB_ID =
            "com.example.ye.gametrade_in.igdb_id";

    Long mIgdbId;

    LinearLayout mDetailLayout;
    ProgressBar mDetailProgress;

    ImageView mCoverImage;
    ProgressBar mCoverProgress;
    TextView mTitle, mSummary, mPopularity;
    Spinner mPlatformSpinner, mRegionSpinner;

    EditText mCreditEditText;
    TextView mCreditEvaluate;

    Button mWishButton, mOfferButton;

    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    GameDetailBean mGameDetail;

    GameTradeService mGameTradeService;

    public FragmentGameDetail newInstance(Long igdbId){
        Bundle args = new Bundle();
        args.putLong(ARG_IGDB_ID, igdbId);

        FragmentGameDetail fragment = new FragmentGameDetail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String authorizedHeader = QueryPreferences
                .getStoredAuthorizedQuery(this.getActivity().getApplicationContext());
        if(authorizedHeader == null) {
            mGameTradeService = GameTradeApi.getClient().create(GameTradeService.class);
        } else {
            mGameTradeService = GameTradeApi
                    .getClient(authorizedHeader)
                    .create(GameTradeService.class);
        }

        mIgdbId = getArguments().getLong(ARG_IGDB_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);

        View v = inflater.inflate(R.layout.fragment_game_detail, container, false);

        mDetailLayout = (LinearLayout) v.findViewById(R.id.game_detail);
        mDetailProgress = (ProgressBar) v.findViewById(R.id.detail_progress);

        mCoverImage = (ImageView) v.findViewById(R.id.detail_cover);
        mCoverProgress = (ProgressBar) v.findViewById(R.id.detail_cover_progress);
        mTitle = (TextView) v.findViewById(R.id.detail_title);
        mSummary = (TextView) v.findViewById(R.id.detail_summary);
        mPopularity = (TextView) v.findViewById(R.id.detail_popularity);
        mPlatformSpinner = (Spinner) v.findViewById(R.id.detail_platform_spinner);
        mRegionSpinner = (Spinner) v.findViewById(R.id.detail_region_spinner);

        mCreditEditText = (EditText) v.findViewById(R.id.detail_credit);
        mCreditEvaluate = (TextView) v.findViewById(R.id.detail_credit_evaluate);


        mWishButton = (Button) v.findViewById(R.id.detail_wish_button);
        mOfferButton = (Button) v.findViewById(R.id.detail_offer_button);


        errorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
        btnRetry = (Button) v.findViewById(R.id.error_btn_retry);
        txtError = (TextView) v.findViewById(R.id.error_txt_cause);

        hideAllLayout();

        //TODO: finish binding buttons

        return v;
    }


    protected void loadGameDetail(){
        setProgressLayout();

        // TODO: finish loadGameDetail()
//        callApi().enqueue(new Callback<GameDetailBean>() {
//            @Override
//            public void onResponse(Call<GameDetailBean> call, Response<GameDetailBean> response) {
//                // Got data. Send it to adapter
//
//                setDetailLayout();
//
//                GameDetailBean result = response.body();
//
//                if(result != null) {
//
//
//                    mAdapter.addAll(results);
//                    if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
//                    else isLastPage = true;
//                } else{
//                    currentPage --;
//                    showNoResultView();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GameDetailBean> call, Throwable t) {
//                t.printStackTrace();
//                showErrorView(t);
//            }
//        });

    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    protected void bindGameDetail(GameDetailBean gameDetail){

        mTitle.setText(gameDetail.getTitle());
        mSummary.setText(gameDetail.getSummary());

        Locale locale = getCurrentLocale();
        mPopularity.setText(String.format(locale, "%.1f", gameDetail.getPopularity()));


//
//        mPlatformSpinner = (Spinner) v.findViewById(R.id.detail_platform_spinner);
//        mRegionSpinner = (Spinner) v.findViewById(R.id.detail_region_spinner);
    }


    /*****************************************************************************************/
    /* Helper Function */


//    private void showDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                if(toMatch){
//                    Intent intent = new Intent();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("matchBean", matchBean);
//                    bundle.putSerializable("gameDetailId", gameDetailId);
//                    intent.putExtras(bundle);
//                    intent.setClass(getActivity(), MatchActivity.class);
//                    try{
//                        startActivity(intent);
//                    }
//                    catch (Exception exc){
//                        showDialog(exc.toString());
//                    }
//                }
//                else if(!toMatch){
//                }
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    Call<GameDetailBean> callApi(){
        return mGameTradeService.getDetailGame(
                mIgdbId
        );
    }

    public void hideAllLayout(){
        if(errorLayout.getVisibility() != View.GONE){
            errorLayout.setVisibility(View.GONE);
        }
        if(mDetailProgress.getVisibility() != View.GONE){
            mDetailProgress.setVisibility(View.GONE);
        }
        if(mDetailLayout.getVisibility() != View.GONE){
            mDetailLayout.setVisibility(View.GONE);
        }
    }

    public void setDetailLayout(){
        if(mDetailLayout.getVisibility() != View.VISIBLE){
            mDetailLayout.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.GONE);
            mDetailProgress.setVisibility(View.GONE);
        }
    }

    public void setProgressLayout(){
        if(mDetailProgress.getVisibility() != View.VISIBLE){
            mDetailProgress.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.GONE);
            mDetailLayout.setVisibility(View.GONE);
        }
    }

    public void setErrorLayout(){
        if(errorLayout.getVisibility() != View.VISIBLE){
            errorLayout.setVisibility(View.VISIBLE);

            mDetailProgress.setVisibility(View.GONE);
            mDetailLayout.setVisibility(View.GONE);
        }
    }


//
//
//    private JSONObject formatModifyJSON(Integer wishPoints) {
//        final JSONObject root = new JSONObject();
//        try {
//            // JSON
//            // {
//            //     "points": wishPoints
//            // }
//            root.put("points", wishPoints);
//            return root;
//        } catch (JSONException exc) {
//            exc.printStackTrace();
//        }
//        return root;
//    }
//
//
//    /*****************************************************************************************/
//    /*  Part for game detail task  */
//
//
//    private class GameDetailTask extends AsyncTask<String, Integer, String> {
//        private String status, urlStr;
//        private int responseCode = -1;
//        public GameDetailBean gameDetail;
//        public MyListBean myList;
//        public Boolean finish = false;
//        @Override
//        protected void onPreExecute() {
//        }
//        @Override
//        protected String doInBackground(String... params) {
//            HttpURLConnection urlConn;
//            try {
//                Uri.Builder builder = new Uri.Builder();
//                builder.appendPath("api")
//                        .appendPath("game")
//                        .appendPath("");
//                urlStr = serverUrl + builder.build().toString()+ gameDetailId;
//                URL url = new URL(urlStr);
//                urlConn = (HttpURLConnection) url.openConnection();
//                // urlConn.setRequestProperty("Authorization", authorizedHeader);
//                urlConn.setRequestMethod("GET");
//                urlConn.connect();
//                InputStream in = urlConn.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                responseCode = urlConn.getResponseCode();
//                JSONProcessor jsonProcessor = new JSONProcessor();
//                gameDetail = jsonProcessor.GetGameDetailSingleBean(reader.readLine());
//
//                switch (operation) {
//                    case "wishList":
//                        urlStr = serverUrl + "api/user/" + userId + "/wishlist/" + gameDetailId;
//                        url = new URL(urlStr);
//                        urlConn = (HttpURLConnection) url.openConnection();
//                        urlConn.setRequestProperty("Authorization", authorizedHeader);
//                        urlConn.setRequestMethod("GET");
//                        urlConn.connect();
//                        in = urlConn.getInputStream();
//                        reader = new BufferedReader(new InputStreamReader(in));
//                        responseCode = urlConn.getResponseCode();
//                        jsonProcessor = new JSONProcessor();
//                        myList = jsonProcessor.GetMyListSingleBean(reader.readLine());
//                        credits = String.valueOf(myList.getPoints());
//                        break;
//                    case "offerList":
//                        urlStr = serverUrl+"api/user/" + userId + "/offerlist/" + gameDetailId;
//                        url = new URL(urlStr);
//                        urlConn = (HttpURLConnection) url.openConnection();
//                        urlConn.setRequestProperty("Authorization", authorizedHeader);
//                        urlConn.setRequestMethod("GET");
//                        urlConn.connect();
//                        in = urlConn.getInputStream();
//                        reader = new BufferedReader(new InputStreamReader(in));
//                        responseCode = urlConn.getResponseCode();
//                        jsonProcessor = new JSONProcessor();
//                        myList = jsonProcessor.GetMyListSingleBean(reader.readLine());
//                        credits = String.valueOf(myList.getPoints());
//                        break;
//                    case "browse":
//                        credits = "0";
//                        break;
//                    default:
//                        break;
//                }
//
//                List<GameReleaseJson> gameReleaseJsons = gameDetail.getReleases();
//                for (int p = 0; p < gameReleaseJsons.size(); p++) {
//                    platformList.add(gameReleaseJsons.get(p).getPlatform());
//                    platformIdList.add(gameReleaseJsons.get(p).getPlatformId());
//                    regionList.add(gameReleaseJsons.get(p).getRegion());
//                    regionIdList.add(gameReleaseJsons.get(p).getRegionId());
//                }
//                setGameDetail(gameDetail.getTitle(), String.valueOf(((int) gameDetail.getPopularity())), gameDetail.getSummary());
//                finish = true;
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            super.onProgressUpdate(progresses);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//
//            }
//            catch (Exception exc){
//                showDialog(exc.toString());
//            }
//            super.onPostExecute(result);
//        }
//    }
//
//
//    private void setGameDetail(String title, String popularity, String text/*String language, String genre, String credits*/) {
//        gameTitleView.setText(title);
//        gamePopularity.setText(popularity);
//        gameTextView.setText(text);
//        //gameCategoryPlatform.setText(platform);
//        //gameCategoryLanguage.setText(language);
//        //gameCategoryGenre.setText(genre);
//        //gameCreditView.setText(credits);
//    }
//
//
//
//    /*****************************************************************************************/
//    /* Part for wish list task */
//
//
//    private class AddToListTask extends AsyncTask<JSONObject, Integer, String> {
//        private String status, urlStr;
//        private JSONObject postJson;
//        private int responseCode = -1;
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected String doInBackground(JSONObject... params) {
//            postJson = params[0];
//            HttpURLConnection urlConn;
//            try {
//                Uri.Builder builder = new Uri.Builder();
//                builder.appendPath("api")
//                        .appendPath("user")
//                        .appendPath(userId)
//                        .appendPath("wishlist");
//                urlStr = serverUrl +builder.build().toString();
//
//                // urlStr = serverUrl + "api/user/" + userId + "/wishlist/";
//
//                URL url = new URL(urlStr);
//                urlConn = (HttpURLConnection) url.openConnection();
//                urlConn.setDoOutput(true);
//                urlConn.setDoInput(true);
//                urlConn.setUseCaches(false);
//                urlConn.setRequestProperty("Authorization", authorizedHeader);
//                urlConn.setRequestMethod("POST");
//                urlConn.setRequestProperty("Content-Type", "application/json");
//                urlConn.connect();
//                OutputStream out = urlConn.getOutputStream();
//                out.write(postJson.toString().getBytes());
//                out.flush();
//                out.close();
//                responseCode = urlConn.getResponseCode();
//                if (responseCode == 200) {
//                    status = "Game has been successfully added to your wish list";
//                } else if (responseCode == 409) {
//                    status = "Your wish list includes this game";
//                } else if (responseCode == 404) {
//                    status = "Connection problem, check connect and login";
//                }else if(responseCode == 401){
//                    status = "Please log in";
//                }
//                else{
//                    status = "Connection problem, check connect and login";
//                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//                status = "Disconnected: " + responseCode;
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            super.onProgressUpdate(progresses);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            showDialog(status);
//            super.onPostExecute(result);
//        }
//    }
//
//
//    private void AddToList(Integer gameId, Integer platformId, Integer regionId, Integer wishPoints) {
//        final JSONObject postJson = formatJSON(gameId, platformId, regionId, wishPoints);
//        new FragmentGameDetail.AddToListTask().execute(postJson);
//    }
//
//
//    /*****************************************************************************************/
//    /* Part for offer list task */
//
//    private class AddToOfferListTask extends AsyncTask<JSONObject, Integer, String> {
//        private String status, urlStr;
//        private JSONObject postJson;
//        private int responseCode = -1;
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected String doInBackground(JSONObject... params) {
//            postJson = params[0];
//            HttpURLConnection urlConn;
//            try {
//                Uri.Builder builder = new Uri.Builder();
//                builder.appendPath("api")
//                        .appendPath("user")
//                        .appendPath(userId)
//                        .appendPath("offerlist");
//                urlStr = serverUrl +builder.build().toString();
//                URL url = new URL(urlStr);
//                urlConn = (HttpURLConnection) url.openConnection();
//
//                // start connection
//                urlConn.setDoOutput(true);
//                urlConn.setDoInput(true);
//                urlConn.setUseCaches(false);
//
//                urlConn.setRequestProperty("Authorization", authorizedHeader);
//
//                urlConn.setRequestMethod("POST");
//                urlConn.setRequestProperty("Content-Type", "application/json");
//                urlConn.connect();
//                OutputStream out = urlConn.getOutputStream();
//                out.write(postJson.toString().getBytes());
//                out.flush();
//                out.close();
//                // upload json
//
//                responseCode = urlConn.getResponseCode();
//                if (responseCode == 200) {
//                    status = "Game has been successfully added to your offer list";
//                } else if (responseCode == 409) {
//                    status = "Your offer list includes this game";
//                } else if (responseCode == 404) {
//                    status = "Connection problem, check connect and login";
//                }
//                else{
//                    status = "Connection problem, check connect and login";
//                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//                status = "Disconnected: " + responseCode;
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            super.onProgressUpdate(progresses);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            showDialog(status);
//            super.onPostExecute(result);
//        }
//    }
//
//
//    private void AddToOfferList(Integer gameId, Integer platformId, Integer regionId,Integer wishPoints) {
//        final JSONObject postJson = formatJSON(gameId, platformId, regionId, wishPoints);
//        new FragmentGameDetail.AddToOfferListTask().execute(postJson);
//    }
//
//
//    /*****************************************************************************************/
//    /* Part for modify task */
//
//    private class ModifyTask extends AsyncTask<JSONObject, Integer, String> {
//        private String status, urlStr;
//        private JSONObject postJson;
//        private int responseCode = -1;
//        private String modifyUrl;
//        @Override
//        protected void onPreExecute() {
//        }
//        @Override
//        protected String doInBackground(JSONObject... params) {
//            postJson = params[0];
//            HttpURLConnection urlConn;
//            try {
//                switch (operation) {
//                    case "browse":
//                        break;
//                    case "wishList":
//                        modifyUrl = "/wishlist/";
//                        break;
//                    case "offerList":
//                        modifyUrl = "/offerlist/";
//                        break;
//                    default:
//                        break;
//                }
//                urlStr = serverUrl + "api/user/" + userId + modifyUrl + gameDetailId + "/modify";
//                URL url = new URL(urlStr);
//                urlConn = (HttpURLConnection) url.openConnection();
//                urlConn.setDoOutput(true);
//                urlConn.setDoInput(true);
//                urlConn.setUseCaches(false);
//
//                urlConn.setRequestProperty("Authorization", authorizedHeader);
//
//                urlConn.setRequestMethod("PUT");
//                urlConn.setRequestProperty("Content-Type", "application/json");
//                urlConn.connect();
//                OutputStream out = urlConn.getOutputStream();
//                out.write(postJson.toString().getBytes());
//                out.flush();
//                out.close();
//                responseCode = urlConn.getResponseCode();
//                if (responseCode == 200) {
//                    status = "Game has been successfully modified";
//                    credits = String.valueOf(addToListPoints);
//                } else if (responseCode == 409) {
//                    status = "Modify failed";
//                } else if (responseCode == 404) {
//                    status = "Modify failed";
//                }
//                else{
//                    status = "Connection problem, check connect and login";
//                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//                status = "Disconnected: " + responseCode;
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            super.onProgressUpdate(progresses);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            UIModify();
//            showDialog(status);
//            super.onPostExecute(result);
//        }
//    }
//
//
//
//    private void Modify(Integer wishPoints) {
//        final JSONObject postJson = formatModifyJSON(wishPoints);
//        new FragmentGameDetail.ModifyTask().execute(postJson);
//    }
//
//
//
//    private void UIModify() {
//        gameCreditView.setText(credits);
//    }
//
//
//
//    /*****************************************************************************************/
//    /* Part for match task */
//
//
//
//    private class MatchCheckTask extends AsyncTask<String, Integer, String> {
//        private String status, urlStr;
//        private int responseCode = -1;
//        public Boolean finish = false;
//        @Override
//        protected void onPreExecute() {
//        }
//        @Override
//        protected String doInBackground(String... params) {
//            HttpURLConnection urlConn;
//            try {
//                urlStr = serverUrl + "api/user/" + userId + "/wishlist/" + gameDetailId + "/match";
//                URL url = new URL(urlStr);
//                urlConn = (HttpURLConnection) url.openConnection();
//
//                urlConn.setRequestProperty("Authorization", authorizedHeader);
//
//                urlConn.setRequestMethod("GET");
//                urlConn.connect();
//                InputStream in = urlConn.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                responseCode = urlConn.getResponseCode();
//                JSONProcessor jsonProcessor = new JSONProcessor();
//                status = reader.readLine();
//                matchBean = jsonProcessor.GetMatchBean(status);
//                switch (matchBean.length){
//                    case 0:
//                        toMatch = false;
//                        status = "Match not found";
//                        break;
//                    default:
//                        toMatch = true;
//                        status = "Match found";
//                        break;
//                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Integer... progresses) {
//            super.onProgressUpdate(progresses);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            showDialog(status);
//            super.onPostExecute(result);
//        }
//    }
//
//    public void Match(){
//        new FragmentGameDetail.MatchCheckTask().execute();
//    }
//

    /*****************************************************************************************/

}
