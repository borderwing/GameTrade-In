package com.example.ye.gametrade_in;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Created by ye on 2017/7/11.
 */

public class MatchActivity extends AppCompatActivity {

    private MatchBean[] matchBean;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchBean = (MatchBean[]) getIntent().getSerializableExtra("matchBean");
        setContentView(R.layout.activity_match);
    }
}
