package com.yamigu.yamigu_app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yamigu.yamigu_app.CustomLayout.InviteFriends;
import com.yamigu.yamigu_app.CustomLayout.ProfileCard;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;

import org.w3c.dom.Text;

public class MypageFragment extends Fragment {
    private Toolbar tb;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProfileCard profileCard;
    private InviteFriends inviteFriends;
    private ImageButton btn_chat_manager;
    private TextView tv_nickname_age, tv_belong, tv_department;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        profileCard = view.findViewById(R.id.profile_card);
        inviteFriends = view.findViewById(R.id.invite_friend);
        btn_chat_manager = view.findViewById(R.id.btn_kakao_chat);
        tv_nickname_age = profileCard.findViewById(R.id.name_and_age);
        tv_belong = profileCard.findViewById(R.id.company);
        tv_department = profileCard.findViewById(R.id.department);

        tv_nickname_age.setText(preferences.getString("nickname", "") + " (" + preferences.getInt("age", 0) +")");
        tv_belong.setText(preferences.getString("belong", ""));
        tv_department.setText(preferences.getString("department", ""));


        return view;
    }

}
