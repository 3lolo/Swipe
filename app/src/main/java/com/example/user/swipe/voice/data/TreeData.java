package com.example.user.swipe.voice.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Grass on 21.02.2016.
 */
public class TreeData implements VoiceInterface, Parcelable {
    String command;
    HashMap<String, String> data;

    public TreeData(String command,String tree,String diameter,String type){
        data = new HashMap<String, String>();
        this.command = command;
        data.put("tree",tree);
        data.put("diameter",diameter);
        data.put("type",type);
    }

    protected TreeData(Parcel in) {
        command = in.readString();
        data = in.readHashMap(HashMap.class.getClassLoader());
    }

    public static final Creator<TreeData> CREATOR = new Creator<TreeData>() {
        @Override
        public TreeData createFromParcel(Parcel in) {
            return new TreeData(in);
        }

        @Override
        public TreeData[] newArray(int size) {
            return new TreeData[size];
        }
    };

    @Override
    public HashMap<String, String> getData() {
        return data;
    }

    @Override
    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean isEquevalent() {
        boolean flag = data.get("tree").length()>0 &&
                       data.get("diameter").length()>0 &&
                       data.get("type").length()>0;
        return flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(command);
        dest.writeMap(data);
    }
    @Override
    public String toString(){
        //return "command : "+command+"\ntree : "+data.get("tree")+"\ndiameter : "+data.get("diameter")+"\ntype : "+data.get("type")+"\n";
        return " "+data.get("tree")+" "+data.get("diameter")+" "+data.get("type")+"\n";
    }
}
