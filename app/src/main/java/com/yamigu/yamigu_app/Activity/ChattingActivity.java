package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yamigu.yamigu_app.Adapter.ChatMessageAdapter;
import com.yamigu.yamigu_app.Etc.ChatData;
import com.yamigu.yamigu_app.R;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar tb;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ArrayAdapter<ChatData> mAdapter;
    private ListView mListView;
    private EditText et_message;
    private ImageButton btn_send_message;
    private TextView tv_title;
    private SharedPreferences preferences;
    private String nickname, date, place, type;
    private int age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nickname = preferences.getString("nickname", "");
        Intent intent = getIntent();

        age = intent.getExtras().getInt("age");
        date = intent.getExtras().getString("date");
        place = intent.getExtras().getString("place");
        type = intent.getExtras().getString("type");

        setSupportActionBar(tb) ;
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        et_message = findViewById(R.id.et_message);
        btn_send_message = findViewById(R.id.btn_send_message);
        btn_send_message.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(date + " || " + place + " || " + type);

        initViews();
        initFirebaseDatabase();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_chat, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_quit:
                // TODO: implement
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_message);
        mAdapter = new ChatMessageAdapter(this, R.layout.chatting_message_recv_woman);
        mListView.setAdapter(mAdapter);
    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message");
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chatData.firebaseKey = dataSnapshot.getKey();
                mAdapter.add(chatData);
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String firebaseKey = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
    @Override
    public void onClick(View v) {
        String message = et_message.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            et_message.setText("");
            ChatData chatData = new ChatData();
            chatData.userName = nickname;
            chatData.message = message;
            chatData.time = System.currentTimeMillis();
            mDatabaseReference.push().setValue(chatData);
        }
    }
}
