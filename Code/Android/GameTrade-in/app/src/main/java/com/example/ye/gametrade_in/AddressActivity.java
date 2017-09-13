package com.example.ye.gametrade_in;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ye.gametrade_in.Bean.AddressBean;
import com.example.ye.gametrade_in.Bean.TradeConfirmBean;
import com.example.ye.gametrade_in.Bean.temp.CreateOrderBean;
import com.example.ye.gametrade_in.activity.OrderActivity;
import com.example.ye.gametrade_in.api.GameTradeApi;
import com.example.ye.gametrade_in.api.GameTradeService;
import com.example.ye.gametrade_in.fragment.AddressPaginationFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ye on 2017/7/11.
 */

public class AddressActivity extends AppCompatActivity {

    private static final String EXTRA_MY_GAME_ID = "AddressActivity.EXTRA_MY_GAME_ID";
    private static final String EXTRA_TARGET_GAME_ID =  "AddressActivity.EXTRA_TARGET_GAME_ID";
    private static final String EXTRA_TARGET_USER_ID =  "AddressActivity.EXTRA_TARGET_USER_ID";
    private static final String EXTRA_OPERATION = "AddressActivity.EXTRA_OPERATION";
    private static final String EXTRA_ORDER_ID = "AddressActivity.EXTRA_ORDER_ID";


    private static final int OPERATION_BROWSE = 0;
    private static final int OPERATION_MATCH = 1;
    private static final int OPERATION_CONFIRM = 2;

    GameTradeInApplication gameTradeInApplication;
    String gameDetailId;

//    private String gameId, targetUserId, operation;

    private Long myGameId;
    private Long targetGameId;
    private Long targetUserId;
    private Long orderId;
    private int operation;

    String serverUrl;
    Integer userId;
    boolean canChoose;
    private AddressBean[] addressBean;
    private ListView listView;
    ProgressBar mMainProgress;
    String authorizedHeader;
    Button addressOperationButton;


    private GameTradeService mGameTradeService;

    public static Intent newIntent(Context context, Long myGameId, Long targetGameId, Long targetUserId){
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(EXTRA_MY_GAME_ID, myGameId);
        intent.putExtra(EXTRA_TARGET_GAME_ID, targetGameId);
        intent.putExtra(EXTRA_TARGET_USER_ID, targetUserId);
        intent.putExtra(EXTRA_OPERATION, OPERATION_MATCH);

        return intent;
    }

    public static Intent newIntent(Context context, Long orderId){
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_OPERATION, OPERATION_CONFIRM);

        return intent;
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(EXTRA_OPERATION, OPERATION_BROWSE);

        return intent;
    }


    private void retrieveFromIntent(Intent intent){
        myGameId = intent.getLongExtra(EXTRA_MY_GAME_ID, 0);
        targetGameId = intent.getLongExtra(EXTRA_TARGET_GAME_ID, 0);
        targetUserId = intent.getLongExtra(EXTRA_TARGET_USER_ID, 0);
        orderId = intent.getLongExtra(EXTRA_ORDER_ID, 0);
        operation = intent.getIntExtra(EXTRA_OPERATION, 0);
    }

    private Fragment createFragment(){
        switch(operation){
            case OPERATION_MATCH:
                return AddressPaginationFragment.newInstance(myGameId, targetGameId, targetUserId);
            case OPERATION_CONFIRM:
                return AddressPaginationFragment.newInstance(orderId);
            default:
                return AddressPaginationFragment.newInstance();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameTradeInApplication = (GameTradeInApplication) getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();


        userId = Integer.valueOf(QueryPreferences.getStoredUserIdQuery(getApplicationContext()));
        authorizedHeader = QueryPreferences.getStoredAuthorizedQuery(getApplicationContext());
        mGameTradeService = GameTradeApi.getClient(authorizedHeader).create(GameTradeService.class);


        setContentView(R.layout.activity_address);

        retrieveFromIntent(getIntent());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.addressToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.findViewById(R.id.action_add);
        // addressOperationButton = (Button) findViewById(R.id.itemAddressOperationButton);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            getMenuInflater().inflate(R.menu.toolbar, menu);
            switch (operation) {
                case OPERATION_BROWSE:
                    menu.findItem(R.id.action_add).setVisible(true);
                    break;
                default:
                    break;
            }
        }
        catch (Exception exc){
            Log.d("",exc.toString());
        }
        return true;
    }


    /*****************************************************************************************/
    /* Toolbar settings */


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String message = "";
            Intent intent = new Intent();
            switch (menuItem.getItemId()){
                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
                case R.id.action_add:
                    intent.putExtra("addressOperation", "add");
                    intent.setClass(AddressActivity.this, AddressOperationActivity.class);
                    startActivity(intent);
            }
            if(!message.equals(""))
            {
                Toast.makeText(AddressActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };



    /*****************************************************************************************/
    /* Helper function */


    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*****************************************************************************************/
}
