package com.example.ye.myfirstproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Goo(View view){
            Show();
    }
    private void Show(){
            TextView goo = (TextView) findViewById(R.id.gooGoo);
            goo.setText(goo.getText()+"Goo! ");
    }

}
