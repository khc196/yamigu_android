package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yamigu.yamigu_app.Adapter.ChatMessageAdapter;
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.Etc.Model.ChatData;
import com.yamigu.yamigu_app.Etc.Model.Conversation;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_PARTNER_MESSAGE = 1;
    private ListMessageAdapter mAdapter;
    private Toolbar tb;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    //private ArrayAdapter<ChatData> mAdapter;
    private ListView mListView;
    private EditText et_message;
    private ImageButton btn_send_message;
    private TextView tv_title;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences preferences;
    private String nickname, date, place, type, matching_id, meeting_id, partner_name, manager_name;
    public static String uid;
    private Uri photoUrl;
    private Conversation conversation;
    public static HashMap<String, Bitmap> bitmapAvataPartner;
    public Bitmap bitmapAvataUser;
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
        meeting_id = intent.getExtras().getString("meeting_id");
        matching_id = intent.getExtras().getString("matching_id");
        partner_name = intent.getExtras().getString("partner_name");
        manager_name = intent.getExtras().getString("manager_name");

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
        conversation = new Conversation();
        et_message = findViewById(R.id.et_message);
        btn_send_message = findViewById(R.id.btn_send_message);
        btn_send_message.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(date + " || " + place + " || " + type);

        initViews();
        initFirebaseDatabase();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            photoUrl = user.getPhotoUrl();
            uid = user.getUid();
        }
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
        //mListView = (ListView) findViewById(R.id.list_message);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(linearLayoutManager);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        //mAdapter = new ChatMessageAdapter(this, R.layout.chatting_message_recv_woman);
        String base64AvataUser = preferences.getString("avata", "");
        if (!base64AvataUser.equals("default")) {
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }
        mAdapter = new ListMessageAdapter(this, conversation, bitmapAvataPartner, bitmapAvataUser);
        //mListView.setAdapter(mAdapter);
        recyclerChat.setAdapter(mAdapter);
        recyclerChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    recyclerChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerChat.smoothScrollToPosition(recyclerChat.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message/" + matching_id);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    ChatData chatData = new ChatData();
                    chatData.userName = (String) mapMessage.get("userName");
                    chatData.idSender = (String) mapMessage.get("idSender");
                    chatData.idReceiver = (String) mapMessage.get("idReceiver");
                    chatData.message = (String) mapMessage.get("message");
                    chatData.time = (long) mapMessage.get("time");
                    conversation.getListMessageData().add(chatData);
                    mAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(conversation.getListMessageData().size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
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
            chatData.idSender = uid;
            chatData.idReceiver = meeting_id;
            chatData.message = message;
            chatData.time = System.currentTimeMillis();
            FirebaseDatabase.getInstance().getReference().child("message/" + matching_id).push().setValue(chatData);
        }
    }
}

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Conversation conversation;
    private HashMap<String, Bitmap> bitmapAvata;
    private HashMap<String, DatabaseReference> bitmapAvataDB;
    private Bitmap bitmapAvataUser;

    public ListMessageAdapter(Context context, Conversation conversation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser) {
        this.context = context;
        this.conversation = conversation;
        this.bitmapAvata = bitmapAvata;
        this.bitmapAvataUser = bitmapAvataUser;
        bitmapAvataDB = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChattingActivity.VIEW_TYPE_PARTNER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.chatting_message_recv_woman, parent, false);
            return new ItemMessagePartnerHolder(view);
        } else if (viewType == ChattingActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.chatting_message_sent_man, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat format = new SimpleDateFormat( "a hh:mm");
        if (holder instanceof ItemMessagePartnerHolder) {
            ((ItemMessagePartnerHolder) holder).usrName.setText(conversation.getListMessageData().get(position).userName);
            ((ItemMessagePartnerHolder) holder).txtContent.setText(conversation.getListMessageData().get(position).message);
            ((ItemMessagePartnerHolder) holder).txtTime.setText(format.format(conversation.getListMessageData().get(position).time));
            Bitmap currentAvata = bitmapAvata.get(conversation.getListMessageData().get(position).idSender);
            if (currentAvata != null) {
                ((ItemMessagePartnerHolder) holder).avata.setImageBitmap(currentAvata);
            } else {
                final String id = conversation.getListMessageData().get(position).idSender;
                if(bitmapAvataDB.get(id) == null){
                    bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                    bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String avataStr = (String) dataSnapshot.getValue();
                                if(!avataStr.equals("default")) {
                                    byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                    ChattingActivity.bitmapAvataPartner.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                }else{
                                    ChattingActivity.bitmapAvataPartner.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.user_profile_no_img));
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        } else if (holder instanceof ItemMessageUserHolder) {
            ((ItemMessageUserHolder) holder).usrName.setText(conversation.getListMessageData().get(position).userName);
            ((ItemMessageUserHolder) holder).txtContent.setText(conversation.getListMessageData().get(position).message);
            ((ItemMessageUserHolder) holder).txtTime.setText(format.format(conversation.getListMessageData().get(position).time));
            if (bitmapAvataUser != null) {
                ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return conversation.getListMessageData().get(position).idSender.equals(ChattingActivity.uid) ? ChattingActivity.VIEW_TYPE_USER_MESSAGE : ChattingActivity.VIEW_TYPE_PARTNER_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return conversation.getListMessageData().size();
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView usrName, txtContent, txtTime;
    public CircularImageView avata;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        usrName = (TextView) itemView.findViewById(R.id.name);
        txtContent = (TextView) itemView.findViewById(R.id.chatting_content);
        txtTime = (TextView) itemView.findViewById(R.id.time);
        avata = (CircularImageView) itemView.findViewById(R.id.iv_profile);
    }
}

class ItemMessagePartnerHolder extends RecyclerView.ViewHolder {
    public TextView usrName, txtContent, txtTime;
    public CircularImageView avata;

    public ItemMessagePartnerHolder(View itemView) {
        super(itemView);
        usrName = (TextView) itemView.findViewById(R.id.name);
        txtContent = (TextView) itemView.findViewById(R.id.chatting_content);
        txtTime = (TextView) itemView.findViewById(R.id.time);
        avata = (CircularImageView) itemView.findViewById(R.id.iv_profile);
    }
}
