package com.example.user.swipe.voice.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.user.swipe.MySphinxActivity;
import com.example.user.swipe.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    private SharedPreferences sp;
    private Set<String> dataName = new HashSet<>();
    private Set<String> dataValue = new HashSet<>();


    @Override
    public void onCreate() {
        super.onCreate();
        taskFin =false;
        context = getApplicationContext();
        commands.add("add_tree");
        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM,0);
        sp = getSharedPreferences(ServiceConstants.APP_NAME, Context.MODE_PRIVATE);
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
                    Log.i(TAG, "AsyncTask: onPostExecute: swtich to the digit search");
                    startWordsRecognition();
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
            recognizer.addGrammarSearch(commands.get(0), tree_grammar);
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
    }

    @Override
    public void onEndOfSpeech() {
        startWordsRecognition();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if( hypothesis == null)
            return;
//        Log.d("recod", "result : " + hypothesis.getHypstr());
        sendIntent("data",""+ hypothesis.getHypstr());

    }


    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        startWordsRecognition();
    }

    private void startWordsRecognition(){

        recognizer.stop();
        try {
            new Thread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recognizer.startListening(commands.get(0), 10);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    private void sendIntent(String key, String data ){
        Intent intent = new Intent();
        intent.setAction(ServiceConstants.WORD_ACTION);
        intent.putExtra(key, data);
        sendBroadcast(intent);
        notify_user();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void notify_user(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Intent notificationIntent = new Intent(context, MySphinxActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                                            0, notificationIntent,
                                            PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent).setContentText("fuck")
                                               .setSmallIcon(R.mipmap.ic_launcher)
        .setDefaults(Notification.DEFAULT_VIBRATE);
        Notification notification = builder.build();
        notificationManager.notify(ServiceConstants.NOTIFY_ID,notification);
        CloseNotify close = new CloseNotify();
        //close.execute(notificationManager);

    }

    class CloseNotify extends AsyncTask<NotificationManager,Void,Void>{
        NotificationManager manager;
        @Override
        protected Void doInBackground(NotificationManager... params) {
            manager = params[0];
            try {
                new Thread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            manager.cancel(ServiceConstants.NOTIFY_ID);
        }
    }
}
