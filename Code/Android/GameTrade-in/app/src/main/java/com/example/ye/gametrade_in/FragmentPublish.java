package com.example.ye.gametrade_in;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ye on 2017/7/3.
 */

public class FragmentPublish extends Fragment{

    EditText gameNameView, gameInfoView, gameCreditView;
    String gameName, gameInfo, gameCredit;
    Button publishButton;
    ImageView gameImageView;
    String serverUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        GameTradeInApplication gameTradeInApplication = (GameTradeInApplication) getActivity().getApplication();
        serverUrl = gameTradeInApplication.getServerUrl();
        return inflater.inflate(R.layout.fragment_publish, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        gameNameView = (EditText) getView().findViewById(R.id.publishName);
        gameInfoView = (EditText) getView().findViewById(R.id.publishInfo);
        gameCreditView = (EditText) getView().findViewById(R.id.publishCredit);
        gameImageView = (ImageView) getView().findViewById(R.id.publishPhoto);

        gameName = gameNameView.getText().toString();
        gameInfo = gameNameView.getText().toString();
        gameCredit = gameCreditView.getText().toString();

        publishButton = (Button) getView().findViewById(R.id.publishButton);
        publishButton.setOnClickListener(onPublishListener);
    }

    private View.OnClickListener onPublishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog("gg");
        }
    };

    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
