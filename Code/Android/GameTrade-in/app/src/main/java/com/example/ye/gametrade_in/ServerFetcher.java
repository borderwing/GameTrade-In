package com.example.ye.gametrade_in;

import android.net.Uri;
import android.util.Log;

import com.example.ye.gametrade_in.Bean.GameTileBean;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhostatSpirit on 10/09/2017.
 */

public class ServerFetcher {
    private static final String TAG = "ServerFetcher";

    private static final Uri ENDPOINT = Uri
            .parse("http://192.168.199.131:8080")
            .buildUpon()
            .appendPath("api")
            .build();

    private String mAuthorizedHeader;

    public ServerFetcher(String authorizedHeader){
        mAuthorizedHeader = authorizedHeader;
    }

    public List<GameTileBean> fetchPopularGameTiles(int pageNumber){
        List<GameTileBean> items = new ArrayList<>();

        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendPath("game")
                .appendPath("trending")
                .appendQueryParameter("limit", String.valueOf(10))
                .appendQueryParameter("offset", String.valueOf(pageNumber));

        try {
            String url = uriBuilder.build().toString();
            String jsonString = getUrlString(url);
            JSONProcessor jsonProcessor = new JSONProcessor();
            items = jsonProcessor.GetGameTileListBean(jsonString);
        } catch (IOException ioe){
            Log.e(TAG, "Failed to fetch game tiles", ioe);
        }

        return items;

    }



    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        if(mAuthorizedHeader != null) {
            connection.setRequestProperty("Authorization", mAuthorizedHeader);
        }


        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }


}
