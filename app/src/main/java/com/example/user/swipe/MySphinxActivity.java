package com.example.user.swipe;

/**
 * Created by Grass on 09.03.2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.user.swipe.voice.data.VoiceInterface;
import com.example.user.swipe.voice.service.BroadcastWorker;
import com.example.user.swipe.voice.service.RecognitionBroadCast;
import com.example.user.swipe.voice.service.RecognitionService;
import com.example.user.swipe.voice.service.ServiceConstants;


/**
 * Created by Grass on 21.02.2016.
 */
public class MySphinxActivity extends Activity implements BroadcastWorker {


    private static final int PERMISSION_RECOD = 21;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        checkPirmission();
        setContentView(R.layout.main);
        registerReceiver(new RecognitionBroadCast(this), new IntentFilter(ServiceConstants.VOICE_ACTION));

    }


    @Override
    public void runCommand(VoiceInterface data) {
        Log.d("recod", "run Command");
        Log.d("recod", data.toString());
    }


    private void checkPirmission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_RECOD);
        }else{
            Intent name = new Intent(this, RecognitionService.class);
            startService(name);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECOD: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startService(new Intent(this, RecognitionService.class));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}