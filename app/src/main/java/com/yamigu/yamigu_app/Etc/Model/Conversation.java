package com.yamigu.yamigu_app.Etc.Model;


import java.util.ArrayList;



public class Conversation {
    private ArrayList<ChatData> listMessageData;
    public Conversation(){
        listMessageData = new ArrayList<>();
    }

    public ArrayList<ChatData> getListMessageData() {
        return listMessageData;
    }
}
