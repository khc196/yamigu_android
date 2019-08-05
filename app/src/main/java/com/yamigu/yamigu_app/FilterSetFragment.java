package com.yamigu.yamigu_app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class FilterSetFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG_DIALOG_EVENT = "dialog_event";
    public static FilterSetFragment getInstance() {
        FilterSetFragment f = new FilterSetFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return inflater.inflate(R.layout.fragment_filter_set, container, false);
    }
    @Override
    public void onClick(View v){

    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.FullScreenDialogStyle);
    }

}
