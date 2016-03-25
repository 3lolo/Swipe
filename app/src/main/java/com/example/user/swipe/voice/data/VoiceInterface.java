package com.example.user.swipe.voice.data;

import java.util.HashMap;

/**
 * Created by Grass on 21.02.2016.
 */
public interface VoiceInterface {

    public HashMap<String, String> getData();
    public void setData(HashMap<String, String> data);
    public String getCommand();
    public void setCommand(String command);
    public boolean isEquevalent();
}
