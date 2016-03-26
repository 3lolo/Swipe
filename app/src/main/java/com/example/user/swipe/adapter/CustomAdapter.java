package com.example.user.swipe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.swipe.R;

import java.util.ArrayList;

/**
 * Created by User on 26.03.2016.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflatter;
    private ArrayList<WordCount> words_list;
    TextView txtTitle;
    TextView txtTitle1;

    public CustomAdapter(Context context,
                      ArrayList<WordCount> words_list) {
        this.context = context;
        this.words_list = words_list;
        this.inflatter = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return words_list.size();
    }

    @Override
    public WordCount getItem(int position) {
        return words_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view==null)
            view = inflatter.inflate(R.layout.item,parent,false);
        WordCount wordCount = getItem(position);

         txtTitle = (TextView) view.findViewById(R.id.textWord);
         txtTitle1 = (TextView) view.findViewById(R.id.textCount);

        txtTitle.setText(wordCount.getWord());
        txtTitle1.setText(String.valueOf(wordCount.getCount()));

        return view;
    }
}
