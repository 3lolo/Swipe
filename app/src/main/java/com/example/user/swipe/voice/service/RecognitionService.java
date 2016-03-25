package com.example.user.swipe.voice.service;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;


import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * Created by Grass on 21.02.2016.
 */
public class
RecognitionService extends Service implements RecognitionListener,
        SoundPool.OnLoadCompleteListener{

    private SpeechRecognizer recognizer;
    private Context context;
    private String TAG = "recod";
    private ArrayList<String> commands = new ArrayList<>();
    private boolean rFlag = false;
    private int bellId;
    private int loadId;
    private SoundPool soundPool;
    private Boolean taskFin;
    //private


    @Override
    public void onCreate() {
        super.onCreate();
        taskFin =false;
        context = getApplicationContext();
        commands.add("command");
        commands.add("add_tree");
        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM,0);
        try {
            bellId = soundPool.load(getAssets().openFd("1897.ogg"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Log.i(TAG, "AsyncTask:doInBackground: setup recognizr");
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.e(TAG, "onPostExecute: failed to init recognizer: " + result);
                } else {
                    soundPool.play(bellId, 1, 1, 0, 0, 1);
                    soundPool.play(bellId, 1, 1, 0, 0, 1);
                    Log.i(TAG, "AsyncTask: onPostExecute: swtich to the digit search");
                    startCommandRecognition();
                    taskFin = true;
                }
            }
        }.execute();


    }

    private void setupRecognizer(File assetsDir) {
        try {
            recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "model"))
                    .setDictionary(new File(assetsDir, "dict/resultDictionary"))
                    .setRawLogDir(assetsDir)
                    .setKeywordThreshold(1e-20f)
                    .getRecognizer();

            recognizer.addListener(this);

            File tree_grammar = new File(assetsDir,"dict/dict_model.jsgf");
            File key_dict     = new File(assetsDir,"dict/keywordDict");
            recognizer.addKeywordSearch(commands.get(0), key_dict);
            recognizer.addGrammarSearch(commands.get(1), tree_grammar);



        } catch (IOException e) {
            Log.d("error",e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!taskFin){
                    try {
                        new Thread().sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try{
                    recognizer.cancel();
                    recognizer.shutdown();
                    stopSelf();
                } catch (Exception ignored){}
            }
        }).start();



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("recod", "onBeginningOfSpeech()");

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("recod", "onEndOfSpeech()");
        startCommandRecognition();
        sendIntent("command", "fin");
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        //   Log.d("recod", "onPartialResult" );
        if (hypothesis == null)
            return;
        if (hypothesis.getHypstr().equals("додати")) {
            startTreeRecognition();
        }
        else{
            sendIntent("data",hypothesis.getHypstr());
        }
        Log.d("recod", "part res : " + hypothesis.getHypstr());

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if( hypothesis == null)
            return;
        Log.d("recod", "result : " + hypothesis.getHypstr());
        if(!hypothesis.getHypstr().equals("додати")){
            Log.d("recod", "result : " + hypothesis.getHypstr());

            sendIntent("data", hypothesis.getHypstr());
        }

    }


    public void startAlert(String value){
        sendIntent("command", "close");
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(this, ServiceDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", value);
        startActivity(intent);

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        startCommandRecognition();
    }

    private void startTreeRecognition(){
        if(rFlag) return ;
        rFlag = true;
        startAlert("");
        soundPool.play(bellId, 1, 1, 0, 0, 1);
        recognizer.stop();
        try {
            new Thread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recognizer.startListening(commands.get(1), 7000);
        //recognizer.

        Log.i(TAG, "start tree");
        rFlag = false;
    }
    private void startCommandRecognition(){
        recognizer.stop();
        recognizer.startListening(commands.get(0));

        Log.i(TAG, "start command");
    }
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    private void sendIntent(String key, String data ){
        Intent intent = new Intent();
        intent.setAction("dialogMessage");
        intent.putExtra(key, data);
        sendBroadcast(intent);
    }
}
