package com.yamigu.yamigu_app.Activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class InviteFriends extends LinearLayout {

    TextView code;
    ImageButton btn_kakao_share;

    public InviteFriends(Context context) {
        super(context);
        initView();
    }
    public InviteFriends(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public InviteFriends(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.invite_friends, this, false);
        addView(v);

        code = (TextView) findViewById(R.id.code);
        btn_kakao_share = (ImageButton) findViewById(R.id.btn_kakao_share);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InviteFriends);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InviteFriends, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {
        String code_string = typedArray.getString(R.styleable.InviteFriends_code);
        code.setText(code_string);
        typedArray.recycle();
    }

    void setCode(String code_resID) {
        code.setText(code_resID);
    }
}
