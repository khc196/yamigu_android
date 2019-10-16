package com.yamigu.yamigu_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yamigu.yamigu_app.Etc.Model.ChatData;
import com.yamigu.yamigu_app.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatMessageAdapter extends ArrayAdapter<ChatData> {
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());

    public ChatMessageAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chatting_message_recv_woman, null);

            viewHolder = new ViewHolder();
            viewHolder.mTxtUserName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.mTxtMessage = (TextView) convertView.findViewById(R.id.chatting_content);
            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatData chatData = getItem(position);
        viewHolder.mTxtUserName.setText(chatData.userName);
        viewHolder.mTxtMessage.setText(chatData.message);
        viewHolder.mTxtTime.setText(mSimpleDateFormat.format(chatData.time));

        return convertView;
    }

    private class ViewHolder {
        private TextView mTxtUserName;
        private TextView mTxtMessage;
        private TextView mTxtTime;
    }
}

