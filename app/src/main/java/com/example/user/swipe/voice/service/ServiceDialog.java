package com.example.user.swipe.voice.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.swipe.voice.data.TreeData;

import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 */
public class ServiceDialog extends Activity {

    public TreeData data;
    public AlertBroadcast receiver;
    private AlertDialog dialog;
    private String message = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("data")) data = getMessage(intent.getStringExtra("data"));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Додати");
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setMessage(data.toString());


        alert.setPositiveButton("ТАК",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMessage();
                        ServiceDialog.this.finish();

                    }
                });
        alert.setNegativeButton("НI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(ServiceConstants.DATA_ACTION, "cancel");
                setResult(RESULT_OK, intent);
                ServiceDialog.this.finish();
            }
        });

        dialog = alert.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);

        textView.setTextSize(35);
        receiver = new AlertBroadcast(this);
        registerReceiver(receiver, new IntentFilter("dialogMessage"));
    }

    public void sendMessage() {

        Intent intent = new Intent();
        intent.setAction(ServiceConstants.VOICE_ACTION);
        intent.putExtra("data", data);
        Log.d("recod", "send "+data.toString());
        sendBroadcast(intent);
    }

    private TreeData getMessage(String str) {
        ArrayList<String> message = new ArrayList(Arrays.asList(str.split(" ")));
        int diametr = 0;
        String type = "";
        String tree = "";

        while (message.size()>0){
            String s =  message.get(0);
            if(!isNumeric(s)) {
                tree += s + " ";
                message.remove(0);
            }else  break;
        }
        while (message.size()>0){
            String s =  message.get(0);
            if(isNumeric(s)) {
                diametr += Integer.parseInt(s) ;
                message.remove(0);
            }
            else break;
        }
        while (message.size()>0){
            type =  message.remove(0);
        }
        String d = "";
        if(diametr!=0)
            d = ""+diametr;
        return new TreeData("додати", tree, d, type);
    }

    public void updateMessage(String message){
        this.message = message;
        data =  getMessage(message);
        dialog.setMessage(data.toString());
    }

    public static boolean isNumeric(String str){
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}