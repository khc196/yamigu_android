package com.yamigu.yamigu_app.CustomLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class MyMeetingCard_Chat extends LinearLayout {
    TextView chat_content, time, unread_count;

    public MyMeetingCard_Chat(Context context) {
        super(context);
        initView();
    }
    public MyMeetingCard_Chat(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public MyMeetingCard_Chat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.my_meeting_card_chat, this, false);
        addView(v);

        chat_content = (TextView) findViewById(R.id.chat_content);
        time = (TextView) findViewById(R.id.time);
        unread_count = (TextView) findViewById(R.id.unread_count);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyMeetingCard_Chat);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyMeetingCard_Chat, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {
        String chat_string = typedArray.getString(R.styleable.MyMeetingCard_Chat_chat);
        chat_content.setText(chat_string);
        String time_string = typedArray.getString(R.styleable.MyMeetingCard_Chat_time);
        time.setText(time_string);
        int unread_count_Integer = typedArray.getInteger(R.styleable.MyMeetingCard_Chat_unread, 0);
        unread_count.setText(Integer.toString(unread_count_Integer));
        if(unread_count_Integer == 0) {
            unread_count.setVisibility(View.INVISIBLE);
        }
        typedArray.recycle();
    }


    public void setChat(int chat_resID) {
        chat_content.setText(chat_resID);
    }
    public void setTime(int time_resID) {
        time.setText(time_resID);
    }
    public void setUnread_count(int unread_count_resID) { unread_count.setText(unread_count_resID); }
}
