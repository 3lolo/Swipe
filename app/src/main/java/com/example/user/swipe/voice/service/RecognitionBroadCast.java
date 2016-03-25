package com.example.user.swipe.voice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.user.swipe.voice.data.VoiceInterface;


/**
 * Created by Grass on 21.02.2016.
 */
public class RecognitionBroadCast extends BroadcastReceiver {

    private BroadcastWorker broadcastInterface;

    public RecognitionBroadCast(BroadcastWorker broadcastInterface){
        this.broadcastInterface = broadcastInterface;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        VoiceInterface voice = intent.getParcelableExtra("data");
        broadcastInterface.runCommand(voice);
    }

}
