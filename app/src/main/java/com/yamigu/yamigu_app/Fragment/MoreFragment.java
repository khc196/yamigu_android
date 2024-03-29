package com.yamigu.yamigu_app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.yamigu.yamigu_app.Activity.AllianceListActivity;
import com.yamigu.yamigu_app.Activity.FAQActivity;
import com.yamigu.yamigu_app.Activity.GuideActivity;
import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Activity.SettingActivity;
import com.yamigu.yamigu_app.Activity.WhatisYamiguActivity;

public class MoreFragment extends Fragment {
    private Toolbar tb;
    private ImageButton btn_whatis_yamigu, btn_alliance_list, btn_guide, btn_faq;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_more, container, false);
//        tb = (Toolbar) view.findViewById(R.id.toolbar_m2) ;
//        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        btn_whatis_yamigu = (ImageButton) view.findViewById(R.id.btn_whatis_yamigu);
        btn_alliance_list = (ImageButton) view.findViewById(R.id.btn_alliance_list);
        btn_guide = (ImageButton) view.findViewById(R.id.btn_guide);
        btn_faq = (ImageButton) view.findViewById(R.id.btn_faq);

        btn_whatis_yamigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WhatisYamiguActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_alliance_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AllianceListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GuideActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FAQActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        if(MainActivity.dialog.isShowing()) {
            MainActivity.dialog.dismiss();
        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_setting, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getContext(), SettingActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
        return true;
    }
}
