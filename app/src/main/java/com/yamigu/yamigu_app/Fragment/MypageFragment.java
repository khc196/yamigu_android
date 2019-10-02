package com.yamigu.yamigu_app.Fragment;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yamigu.yamigu_app.CustomLayout.InviteFriends;
import com.yamigu.yamigu_app.CustomLayout.ProfileCard;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MypageFragment extends Fragment {
    private Toolbar tb;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProfileCard profileCard;
    private InviteFriends inviteFriends;
    private ImageButton btn_chat_manager;
    private TextView tv_nickname, tv_age, tv_belong, tv_department, tv_available_nickname;
    private EditText et_nickname;
    private ImageButton btn_edit_nickname;
    private String auth_token;
    private boolean validated_from_server;
    private boolean nickname_validated;
    private String nickname;
    private Menu globalMenu;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        profileCard = view.findViewById(R.id.profile_card);
        inviteFriends = view.findViewById(R.id.invite_friend);
        btn_chat_manager = view.findViewById(R.id.btn_kakao_chat);
        tv_nickname = profileCard.findViewById(R.id.name);
        tv_age = profileCard.findViewById(R.id.age);
        tv_belong = profileCard.findViewById(R.id.company);
        tv_department = profileCard.findViewById(R.id.department);
        tv_available_nickname = profileCard.findViewById(R.id.tv_available_nickname);
        et_nickname = profileCard.findViewById(R.id.et_nickname);
        btn_edit_nickname = profileCard.findViewById(R.id.btn_edit_nickname);

        tv_nickname.setText(preferences.getString("nickname", ""));
        tv_age.setText(" (" + preferences.getInt("age", 0) + ")");
        tv_belong.setText(preferences.getString("belong", ""));
        tv_department.setText(preferences.getString("department", ""));
        auth_token = preferences.getString("auth_token", "");
        btn_edit_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_nickname.setHint(preferences.getString("nickname", ""));
                tv_nickname.setVisibility(View.GONE);
                et_nickname.setVisibility(View.VISIBLE);
                btn_edit_nickname.setVisibility(View.GONE);
                globalMenu.findItem(R.id.menu_cancel).setVisible(true);
                globalMenu.findItem(R.id.menu_complete).setVisible(true);
            }
        });
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String url = "http://147.47.208.44:9999/api/validation/nickname/"+editable.toString();
                ContentValues values = new ContentValues();
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        });
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_edit_nickname, menu);
        globalMenu = menu;
        menu.findItem(R.id.menu_complete).setVisible(false);
        menu.findItem(R.id.menu_cancel).setVisible(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_complete:
                if(nickname_validated) {
                    String url = "http://147.47.208.44:9999/api/user/change/nickname/";
                    ContentValues values = new ContentValues();
                    values.put("nickname", et_nickname.getText().toString());
                    NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                    networkTask2.execute();
                    tv_nickname.setVisibility(View.VISIBLE);
                    et_nickname.setVisibility(View.GONE);
                    btn_edit_nickname.setVisibility(View.VISIBLE);
                    tv_available_nickname.setVisibility(View.GONE);
                    globalMenu.findItem(R.id.menu_cancel).setVisible(false);
                    globalMenu.findItem(R.id.menu_complete).setVisible(false);
                }
                return true;
            case R.id.menu_cancel:
                tv_nickname.setVisibility(View.VISIBLE);
                et_nickname.setVisibility(View.GONE);
                btn_edit_nickname.setVisibility(View.VISIBLE);
                tv_available_nickname.setVisibility(View.GONE);
                globalMenu.findItem(R.id.menu_cancel).setVisible(false);
                globalMenu.findItem(R.id.menu_complete).setVisible(false);
                return true;
        }
        return true;
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

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            validated_from_server = false;

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                validated_from_server = jsonObject.getBoolean("is_available");
            } catch(JSONException e) {
                validated_from_server = false;
            } catch(NullPointerException e) {
                validated_from_server = false;
            }

            String pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
            Editable editable = et_nickname.getText();
            boolean is_validate = editable.length() <= 6 && Pattern.matches(pattern, editable.toString());
            if(!editable.toString().isEmpty() || !editable.toString().equals(tv_nickname.getText().toString())) {
                tv_available_nickname.setVisibility(View.VISIBLE);
                if (!is_validate || !validated_from_server) {
                    tv_available_nickname.setTextColor(getResources().getColor(R.color.colorRed));
                    tv_available_nickname.setText("사용 불가능합니다.");
                    nickname_validated = false;
                    nickname = "";
                } else {
                    tv_available_nickname.setTextColor(getResources().getColor(R.color.colorBlue));
                    tv_available_nickname.setText("사용 가능합니다.");
                    nickname_validated = true;
                    nickname = editable.toString();
                }
            }
            else {
                tv_available_nickname.setVisibility(View.GONE);
            }
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
            String result;
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "POST", auth_token);
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            String after_nickname = "";
            try {
                jsonObject = new JSONObject(s);
                after_nickname = jsonObject.getString("data");
            } catch(JSONException e) {
                e.printStackTrace();
            }
            tv_nickname.setText(after_nickname);
            editor.putString("nickname", after_nickname);

        }
    }
}
