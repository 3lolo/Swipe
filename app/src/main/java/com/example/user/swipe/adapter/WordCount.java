package com.example.user.swipe.adapter;

/**
 * Created by User on 26.03.2016.
 */
public class WordCount {
    String word;
    int count;

    public WordCount(int count, String word) {
        this.count = count;
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
