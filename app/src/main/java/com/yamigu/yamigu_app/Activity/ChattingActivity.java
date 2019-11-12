package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.ContentValues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import android.os.AsyncTask;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.CustomLayout.MeetingCancelDialog;
import com.yamigu.yamigu_app.Etc.Model.ChatData;
import com.yamigu.yamigu_app.Etc.Model.Conversation;
import com.yamigu.yamigu_app.Etc.Model.ReceivedMessage;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import com.yamigu.yamigu_app.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static java.security.AccessController.getContext;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener{
    public static RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_PARTNER_MESSAGE = 1;
    public static final int VIEW_TYPE_MANAGER_MESSAGE = 2;
    public static final String MANAGER_WELCOME_TAG = "###manager-welcome-content###";
    public static final String MANAGER_PRECAUTIONS_TAG = "###manager-precautions-content###";
    public static final String MANAGER_PLACE_TAG = "###manager-place-content###";
    private ListMessageAdapter mAdapter;
    private Toolbar tb;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference, receivedChatReference;
    private ChildEventListener mChildEventListener;
    //private ArrayAdapter<ChatData> mAdapter;
    private ListView mListView;
    private EditText et_message;
    private ImageButton btn_send_message;
    private TextView tv_title;
    private LinearLayoutManager linearLayoutManager;
    public static SharedPreferences preferences;

    public static String nickname, date, date_day, place, type, matching_id, meeting_id, partner_name, manager_name, manager_name_orig;

    private long accepted_at;
    public static String uid, partner_uid, manager_uid;
    private Uri photoUrl;
    private Conversation conversation;
    public static HashMap<String, Bitmap> bitmapAvataPartner;
    public Bitmap bitmapAvataUser;
    public static int gender; // my gender
    public static int partner_age;
    public static String partner_belong, partner_department;

    private String auth_token;
    private MeetingCancelDialog meetingCancelDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        nickname = preferences.getString("nickname", "");

        auth_token = preferences.getString("auth_token", "");

        Intent intent = getIntent();

        partner_age = intent.getExtras().getInt("partner_age");
        partner_belong = intent.getExtras().getString("partner_belong");
        partner_department = intent.getExtras().getString("partner_department");
        partner_name = intent.getExtras().getString("partner_nickname");
        date = intent.getExtras().getString("date");
        date_day = date.split(" ")[1];
        place = intent.getExtras().getString("place");
        type = intent.getExtras().getString("type");
        meeting_id = intent.getExtras().getString("meeting_id");
        matching_id = intent.getExtras().getString("matching_id");

        manager_name_orig = intent.getExtras().getString("manager_name");
        manager_name = "야미구 매니저 " + manager_name_orig;


        partner_uid = intent.getExtras().getString("partner_uid");
        manager_uid = intent.getExtras().getString("manager_uid");
        accepted_at = intent.getExtras().getLong("accepted_at");
        gender = preferences.getInt("gender", 0);
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
        uid = preferences.getString("uid", "");
        initViews();
        ChatData auto_Chat1 = new ChatData();
        ChatData auto_Chat2 = new ChatData();
        ChatData auto_Chat3 = new ChatData();
        auto_Chat1.userName = manager_name;
        auto_Chat1.idSender = manager_uid;
        auto_Chat1.message = MANAGER_WELCOME_TAG;
        auto_Chat1.time = accepted_at;

        auto_Chat2.userName = manager_name;
        auto_Chat2.idSender = manager_uid;
        auto_Chat2.message = MANAGER_PRECAUTIONS_TAG;
        auto_Chat2.time = accepted_at;

        conversation.getListMessageData().add(auto_Chat1);
        conversation.getListMessageData().add(auto_Chat2);
        mAdapter.notifyDataSetChanged();
        initFirebaseDatabase();
        linearLayoutManager.scrollToPosition(conversation.getListMessageData().size() - 1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            photoUrl = user.getPhotoUrl();
        }

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);

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
                Dialog();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    private void Dialog() {
        meetingCancelDialog = new MeetingCancelDialog(ChattingActivity.this);
        meetingCancelDialog.setCancelable(true);
        meetingCancelDialog.setCanceledOnTouchOutside(true);


        meetingCancelDialog.getWindow().setGravity(Gravity.CENTER);
        meetingCancelDialog.show();

        meetingCancelDialog.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetingCancelDialog.dismiss();
            }
        });
        meetingCancelDialog.btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://106.10.39.154:9999/api/matching/cancel_matching/";
                ContentValues values = new ContentValues();
                values.put("match_id", matching_id);
                NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                networkTask2.execute();
            }
        });
    }
    private void initViews() {
        //mListView = (ListView) findViewById(R.id.list_message);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(linearLayoutManager);


        //mAdapter = new ChatMessageAdapter(this, R.layout.chatting_message_recv_woman);
        String base64AvataUser = preferences.getString("avata", "");
        if (!base64AvataUser.equals("default")) {
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }
        bitmapAvataPartner = new HashMap<>();
        mAdapter = new ListMessageAdapter(this, conversation, bitmapAvataPartner, bitmapAvataUser);
        //mListView.setAdapter(mAdapter);
        recyclerChat.setAdapter(mAdapter);
        recyclerChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(recyclerChat.getAdapter().getItemCount() - 1 >= 0) {
                    if (bottom < oldBottom) {
                        recyclerChat.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerChat.smoothScrollToPosition(recyclerChat.getAdapter().getItemCount() - 1);
                            }
                        }, 100);
                    }
                }
            }
        });
    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message/" + matching_id);
        receivedChatReference = mFirebaseDatabase.getReference("user/" + uid  + "/receivedMessages/" + matching_id);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    try {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        ChatData chatData = new ChatData();
                        chatData.id = (String) mapMessage.get("id");
                        chatData.userName = (String) mapMessage.get("userName");
                        chatData.idSender = (String) mapMessage.get("idSender");
                        chatData.message = (String) mapMessage.get("message");
                        chatData.time = (long) mapMessage.get("time");
                        conversation.getListMessageData().add(chatData);
                        if(!chatData.idSender.equals(uid)) {
                            ReceivedMessage receivedMessage = new ReceivedMessage();
                            receivedMessage.id = chatData.id;
                            receivedMessage.isUnread = false;
                            try {
                                receivedChatReference.child(chatData.id).setValue(receivedMessage);
                            } catch(NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(conversation.getListMessageData().size() - 1);
                    } catch(ClassCastException e) {
                        e.printStackTrace();
                    }
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
            chatData.message = message;
            chatData.time = System.currentTimeMillis();
            DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("message/" + matching_id);
            DatabaseReference partnerRef = FirebaseDatabase.getInstance().getReference().child("user/" + partner_uid + "/receivedMessages");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user/" + uid + "/receivedMessages");
            String key = messageRef.push().getKey();
            chatData.id = key;
            messageRef.child(key).setValue(chatData);
            ReceivedMessage receivedMessage = new ReceivedMessage();
            receivedMessage.id = key;
            receivedMessage.isUnread = true;
            partnerRef.child(matching_id).child(key).setValue(receivedMessage);
            receivedMessage.isUnread = false;
            userRef.child(matching_id).child(key).setValue(receivedMessage);
            String url = "http://106.10.39.154:9999/api/fcm/send_push/";
            ContentValues values = new ContentValues();
            values.put("receiverId", partner_uid);
            values.put("message", message);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("activity", "ChattingActivity");
                JSONObject data = new JSONObject();
                JSONObject intent_args = new JSONObject();
                intent_args.put("partner_age", preferences.getInt("age", 0));
                intent_args.put("partner_belong", preferences.getString("belong", ""));
                intent_args.put("partner_department", preferences.getString("department", ""));
                intent_args.put("partner_nickname", preferences.getString("nickname", ""));
                intent_args.put("date", date);
                intent_args.put("place", place);
                intent_args.put("type", type);
                intent_args.put("meeting_id", meeting_id);
                intent_args.put("matching_id", matching_id);
                intent_args.put("manager_name", manager_name_orig);
                intent_args.put("partner_uid", uid);
                intent_args.put("manager_uid", manager_uid);
                intent_args.put("accepted_at", accepted_at);
                data.put("title", nickname);
                data.put("content", message);
                data.put("clickAction", ".ChattingActivity");
                data.put("intentArgs", intent_args);
                Log.d("SendData", data.toString());
                values.put("data", data.toString());
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int count_me = 0;
            for(int i =0; i < conversation.getListMessageData().size(); i++) {
                if(conversation.getListMessageData().get(i).idSender.equals(uid)) {
                    count_me++;
                }
            }
            if(count_me == 0) {
                for(int i =0; i < conversation.getListMessageData().size(); i++) {
                    if(conversation.getListMessageData().get(i).idSender.equals(partner_uid)) {
                        ChatData chatData_place = new ChatData();
                        chatData_place.userName = manager_name;
                        chatData_place.idSender = manager_uid;
                        chatData_place.message = MANAGER_PLACE_TAG;
                        chatData_place.time = System.currentTimeMillis();
                        String key2 = messageRef.push().getKey();
                        chatData_place.id = key2;
                        messageRef.child(key2).setValue(chatData_place);

                        break;
                    }
                }
            }
        }
    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            finish();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
            View view;
            if (ChattingActivity.gender == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.chatting_message_recv_woman, parent, false);
            }
            else {
                view = LayoutInflater.from(context).inflate(R.layout.chatting_message_recv_man, parent, false);
            }
            return new ItemMessagePartnerHolder(view);
        } else if (viewType == ChattingActivity.VIEW_TYPE_USER_MESSAGE) {
            View view;
            if (ChattingActivity.gender == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.chatting_message_sent_man, parent, false);
            }
            else {
                view = LayoutInflater.from(context).inflate(R.layout.chatting_message_sent_woman, parent, false);
            }
            return new ItemMessageUserHolder(view);
        } else if (viewType == ChattingActivity.VIEW_TYPE_MANAGER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.chatting_message_recv_manager, parent, false);
            return new ItemMessageManagerHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat format = new SimpleDateFormat( "a h:mm");
        if (holder instanceof ItemMessagePartnerHolder) {
            ((ItemMessagePartnerHolder) holder).usrName.setText(conversation.getListMessageData().get(position).userName);
            ((ItemMessagePartnerHolder) holder).txtContent.setText(conversation.getListMessageData().get(position).message);
            ((ItemMessagePartnerHolder) holder).txtTime.setText(format.format(conversation.getListMessageData().get(position).time));
            Bitmap currentAvata;
            try {
                currentAvata = bitmapAvata.get(conversation.getListMessageData().get(position).idSender);
            } catch(NullPointerException e){
                e.printStackTrace();
                currentAvata = null;
            }
            if (currentAvata != null) {
                ((ItemMessagePartnerHolder) holder).avata.setImageBitmap(currentAvata);
            } else {
                final String id = conversation.getListMessageData().get(position).idSender;
                if(bitmapAvataDB.get(id) == null){
                    try {
                        bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                        bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String avataStr = (String) dataSnapshot.getValue();
                                    if (!avataStr.equals("default")) {
                                        byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                        ChattingActivity.bitmapAvataPartner.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
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
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        } else if (holder instanceof ItemMessageManagerHolder) {
            ((ItemMessageManagerHolder) holder).setType(conversation.getListMessageData().get(position).message);
            ((ItemMessageManagerHolder) holder).usrName.setText(conversation.getListMessageData().get(position).userName);
            ((ItemMessageManagerHolder) holder).txtTime.setText(format.format(conversation.getListMessageData().get(position).time));
            ((ItemMessageManagerHolder) holder).txtContent.setText(conversation.getListMessageData().get(position).message);
            Bitmap currentAvata;
            try {
                currentAvata = bitmapAvata.get(conversation.getListMessageData().get(position).idSender);
            } catch(NullPointerException e){
                e.printStackTrace();
                currentAvata = null;
            }
            if (currentAvata != null) {
                ((ItemMessageManagerHolder) holder).avata.setImageBitmap(currentAvata);
            } else {
                final String id = conversation.getListMessageData().get(position).idSender;
                if(bitmapAvataDB.get(id) == null){
                    try {
                        bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                        bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String avataStr = (String) dataSnapshot.getValue();
                                    if (!avataStr.equals("default")) {
                                        byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                        ChattingActivity.bitmapAvataPartner.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
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
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
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
        int view_type = -1;
        if(conversation.getListMessageData().get(position).idSender.equals(ChattingActivity.uid)) {
            view_type = ChattingActivity.VIEW_TYPE_USER_MESSAGE;
        }
        else if(conversation.getListMessageData().get(position).idSender.equals(ChattingActivity.partner_uid)) {
            view_type = ChattingActivity.VIEW_TYPE_PARTNER_MESSAGE;
        }
        else if(conversation.getListMessageData().get(position).idSender.equals(ChattingActivity.manager_uid)) {
            view_type = ChattingActivity.VIEW_TYPE_MANAGER_MESSAGE;
        }
        return view_type;
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

class ItemMessageManagerHolder extends RecyclerView.ViewHolder {
    private LinearLayout content_welcome, content_precautions, content_place, content_normal;
    public TextView usrName, txtContent, txtTime;
    public CircularImageView avata;

    public ItemMessageManagerHolder(View itemView) {
        super(itemView);
        content_welcome = (LinearLayout) itemView.findViewById(R.id.content_welcome);
        content_precautions = (LinearLayout) itemView.findViewById(R.id.content_precautions);
        content_place = (LinearLayout) itemView.findViewById(R.id.content_place);
        content_normal = (LinearLayout) itemView.findViewById(R.id.content_normal);
        content_welcome.setVisibility(View.GONE);
        content_precautions.setVisibility(View.GONE);
        content_place.setVisibility(View.GONE);
        content_normal.setVisibility(View.GONE);
        usrName = (TextView) itemView.findViewById(R.id.name);
        txtContent = (TextView) itemView.findViewById(R.id.chatting_content);
        txtTime = (TextView) itemView.findViewById(R.id.time);
        avata = (CircularImageView) itemView.findViewById(R.id.iv_profile);
    }
    public void setType(String Type) {
        if(Type.equals(ChattingActivity.MANAGER_WELCOME_TAG)) {
            content_precautions.setVisibility(View.GONE);
            content_place.setVisibility(View.GONE);
            content_normal.setVisibility(View.GONE);
            content_welcome.setVisibility(View.VISIBLE);
            TextView chatting_content_name_and_age_man, chatting_content_name_and_age_woman, chatting_content_belong_man, chatting_content_belong_woman, chatting_content_date, chatting_content_place, chatting_content_type;
            chatting_content_name_and_age_man = content_welcome.findViewById(R.id.chatting_content_name_and_age_man);
            chatting_content_belong_man = content_welcome.findViewById(R.id.chatting_content_belong_man);
            chatting_content_name_and_age_woman = content_welcome.findViewById(R.id.chatting_content_name_and_age_woman);
            chatting_content_belong_woman = content_welcome.findViewById(R.id.chatting_content_belong_woman);
            chatting_content_date = content_welcome.findViewById(R.id.chatting_content_date);
            chatting_content_place = content_welcome.findViewById(R.id.chatting_content_place);
            chatting_content_type = content_welcome.findViewById(R.id.chatting_content_type);
            if(ChattingActivity.gender == 1) {
                chatting_content_name_and_age_man.setText(ChattingActivity.nickname + "(" + ChattingActivity.preferences.getInt("age", 0) + ")");
                chatting_content_belong_man.setText(ChattingActivity.preferences.getString("belong", "") + " " + ChattingActivity.preferences.getString("department", ""));
                chatting_content_name_and_age_woman.setText(ChattingActivity.partner_name + "(" + ChattingActivity.partner_age + ")");
                chatting_content_belong_woman.setText(ChattingActivity.partner_belong + " " + ChattingActivity.partner_department);

            }
            else {
                chatting_content_name_and_age_woman.setText(ChattingActivity.nickname + "(" + ChattingActivity.preferences.getInt("age", 0) + ")");
                chatting_content_belong_woman.setText(ChattingActivity.preferences.getString("belong", "") + " " + ChattingActivity.preferences.getString("department", ""));
                chatting_content_name_and_age_man.setText(ChattingActivity.partner_name + "(" + ChattingActivity.partner_age + ")");
                chatting_content_belong_man.setText(ChattingActivity.partner_belong + " " + ChattingActivity.partner_department);
            }
            chatting_content_date.setText(ChattingActivity.date_day);
            chatting_content_place.setText(ChattingActivity.place);
            String type_name = "";
            if(ChattingActivity.type.equals("2:2")) {
                type_name = "2:2 소개팅";
            }
            else if(ChattingActivity.type.equals("3:3")) {
                type_name = "3:3 미팅";
            }
            else if(ChattingActivity.type.equals("4:4")) {
                type_name = "4:4 소개팅";
            }
            chatting_content_type.setText(type_name);
        }
        else if(Type.equals(ChattingActivity.MANAGER_PRECAUTIONS_TAG)) {
            content_welcome.setVisibility(View.GONE);
            content_place.setVisibility(View.GONE);
            content_normal.setVisibility(View.GONE);
            content_precautions.setVisibility(View.VISIBLE);
            String str = "안녕하세요 :)\n미팅 진행을 도와줄 매칭 매니저 sub입니다.\n\n아래 내용을 꼭 참고해주세요\n*채팅내에서 자유롭게 약속을 수정하셔도 됩니다.\n" +
                    "\n*개인 연락처 교환시 야미구 미팅에서 벗어나 모든 책임 및 환불제도에서 제외됩니다.\n" +
                    "\n\n매니저가 필요하거나 궁금한점이 생기면 언제든 우측 상단 아이콘을 눌러주세요!";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7B22")), 20, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7B22")), 68, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7B22")), 107, 122, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7B22")), 153, 163, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView precaution_text = content_precautions.findViewById(R.id.precaution_text);
            precaution_text.setText(ssb);

        }
        else if(Type.equals(ChattingActivity.MANAGER_PLACE_TAG)) {
            content_welcome.setVisibility(View.GONE);
            content_precautions.setVisibility(View.GONE);
            content_normal.setVisibility(View.GONE);
            content_place.setVisibility(View.VISIBLE);
        }
        else {
            content_welcome.setVisibility(View.GONE);
            content_precautions.setVisibility(View.GONE);
            content_place.setVisibility(View.GONE);
            content_normal.setVisibility(View.VISIBLE);
        }
    }

}
