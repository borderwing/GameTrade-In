package com.example.ye.gametrade_in;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar);
        toolbar.setNavigationIcon(R.drawable.nav);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

    }

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
            Intent intent;
            switch (menuItem.getItemId()){
                case R.id.action_myList:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, MyListActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;
                case R.id.action_publish:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, PublishActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_gameDetail:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, GameDetailActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_register:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_orderDetail:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, OrderDetailActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_login:
                    intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                case R.id.action_search:
                    message += "Click search";
                    break;
                case R.id.action_settings:
                    message += "Click setting";
                    break;
            }
            if(!message.equals(""))
            {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
