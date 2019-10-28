package com.yamigu.yamigu_app.CustomLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class ProfileCard extends LinearLayout {

    FrameLayout bg;
    TextView name, age, company, department;
    public ImageButton btn_edit_nickname;

    public ProfileCard(Context context) {
        super(context);
        initView();
    }
    public ProfileCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public ProfileCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.profile_card, this, false);
        addView(v);

        bg = (FrameLayout) findViewById(R.id.bg);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        company = (TextView) findViewById(R.id.company);
        department = (TextView) findViewById(R.id.department);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ProfileCard);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ProfileCard, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {

        String name_string = typedArray.getString(R.styleable.ProfileCard_nickname);
        name.setText(name_string);

        String age_string = typedArray.getString(R.styleable.ProfileCard_age);
        age.setText(age_string);

        String company_string = typedArray.getString(R.styleable.ProfileCard_company);
        company.setText(company_string);

        String department_string = typedArray.getString(R.styleable.ProfileCard_department);
        department.setText(department_string);

        typedArray.recycle();
    }


    public void setCompany(int company_resID) {
        company.setText(company_resID);
    }
    public void setDepartment(int department_resID) { department.setText(department_resID);
    }
}
