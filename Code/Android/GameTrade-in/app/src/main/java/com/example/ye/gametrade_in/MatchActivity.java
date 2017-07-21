package com.example.ye.gametrade_in;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.MatchBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ye on 2017/7/11.
 */

public class MatchActivity extends AppCompatActivity {


    String gameId, targetUserId;
    String gameDetailId;
    private MatchBean[] matchBean;
    private ListView listView;
    boolean chooseAddress = false;
    String authorizedHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchBean = (MatchBean[]) getIntent().getSerializableExtra("matchBean");
        gameDetailId = (String) getIntent().getSerializableExtra("gameDetailId");
        setContentView(R.layout.activity_match);

        listView = (ListView) findViewById(R.id.matchListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.matchToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        SimpleAdapter adapter = new SimpleAdapter(this, getMatchData(matchBean), R.layout.item_match,
                new String[]{"itemMatchSenderId", "itemMatchGetGameId", "itemMatchOfferGameId"},
                new int[]{R.id.itemMatchSenderId, R.id.itemMatchGetGameId, R.id.itemMatchOfferGameId}
        );

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onMatchItemClickListener);
    }


    /*****************************************************************************************/
    /* ListView item click settings */


    private AdapterView.OnItemClickListener onMatchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
            targetUserId = map.get("itemMatchSenderId");
            gameId = map.get("itemMatchOfferGameId");
            chooseAddress = true;
            showDialog("Please choose address");
        }
    };



    /*****************************************************************************************/
    /* Go to choose address */


    private void ChooseAddress(String gameId, String targetUserId, String gameDetailId){
        Intent intent = new Intent();
        intent.putExtra("gameId", gameId);
        intent.putExtra("targetUserId", targetUserId);
        intent.putExtra("gameDetailId", gameDetailId);
        intent.putExtra("operation", "match");
        intent.setClass(MatchActivity.this, AddressActivity.class);
        startActivity(intent);
    }



    /*****************************************************************************************/
    /* Toolbar settings */


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String message = "";
            Intent intent;
            switch (menuItem.getItemId()){
                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(MatchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };




    /*****************************************************************************************/
    // For setting match list


    private List<Map<String, Object>> getMatchData(MatchBean[] matchBean){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        for(int i = 0; i < matchBean.length; i++){
            map = new HashMap<String, Object>();
            map.put("itemMatchSenderId", matchBean[i].getSenderId());
            map.put("itemMatchGetGameId", matchBean[i].getGetGameId());
            map.put("itemMatchOfferGameId", matchBean[i].getOfferGameId());
            list.add(map);
        }
        return list;
    }



    /*****************************************************************************************/
    /* Helper function */


    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                if(chooseAddress){
                    ChooseAddress(gameId, targetUserId, gameDetailId);
                }
                else if(!chooseAddress){
                    ;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*****************************************************************************************/
}
