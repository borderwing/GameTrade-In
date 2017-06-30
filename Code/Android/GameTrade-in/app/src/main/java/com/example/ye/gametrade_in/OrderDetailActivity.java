package com.example.ye.gametrade_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.orderDetailToolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        ImageButton button = (ImageButton) findViewById(R.id.homeButton);
        button.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(OrderDetailActivity.this, MainActivity.class);
            startActivity(intent);
            OrderDetailActivity.this.finish();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String message = "";
            switch (menuItem.getItemId()){
                case R.id.action_myList:
                    Intent intent = new Intent();
                    intent.setClass(OrderDetailActivity.this, MyListActivity.class);
                    startActivity(intent);
                    OrderDetailActivity.this.finish();
                    break;
                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
                case R.id.action_HomeButton:
                    intent = new Intent();
                    intent.setClass(OrderDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    OrderDetailActivity.this.finish();
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
