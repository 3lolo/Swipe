package com.example.user.swipe.voice.service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Grass on 09.03.2016.
 */
public class AlertBroadcast extends BroadcastReceiver {

    private ServiceDialog dialog;

    public AlertBroadcast(ServiceDialog dialog){
        this.dialog = dialog;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String data     = intent.getStringExtra("data");
        String command  = intent.getStringExtra("command");
        if(data!=null)
            dialog.updateMessage(data);
        if(command!=null && command.equals("close"))
            dialog.finish();
        if (command!=null && command.equals("fin") && !dialog.data.isEquevalent()){
            dialog.finish();
        }
    }

}
