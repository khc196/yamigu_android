package com.yamigu.yamigu_app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yamigu.yamigu_app.Etc.ChatMessage;
import com.yamigu.yamigu_app.R;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter {
    List msgs = new ArrayList();
    public ChatMessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public void add(ChatMessage object){
        msgs.add(object);
        super.add(object);
    }
    @Override
    public int getCount() {
        return msgs.size();
    }
    @Override
    public ChatMessage getItem(int index) {
        return (ChatMessage) msgs.get(index);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
//        if (row == null) {
//            // inflator를 생성하여, chatting_message.xml을 읽어서 View객체로 생성한다.
//            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            row = inflater.inflate(R.layout.chatting_message, parent, false);
//        }
//        // Array List에 들어 있는 채팅 문자열을 읽어
//        ChatMessage msg = (ChatMessage) msgs.get(position);
//        // Inflater를 이용해서 생성한 View에, ChatMessage를 삽입한다.
//        TextView msgText = (TextView) row.findViewById(R.id.chatmessage);
//        msgText.setText(msg.getMessage());
//        msgText.setTextColor(Color.parseColor("#000000"));

        return row;
    }
}

