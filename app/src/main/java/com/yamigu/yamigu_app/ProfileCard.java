package com.yamigu.yamigu_app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileCard extends LinearLayout {

    LinearLayout bg;
    TextView name_and_age, company, department;


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

        bg = (LinearLayout) findViewById(R.id.bg);
        name_and_age = (TextView) findViewById(R.id.name_and_age);
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

        String profile_string = typedArray.getString(R.styleable.ProfileCard_name_and_age);
        name_and_age.setText(profile_string);

        String company_string = typedArray.getString(R.styleable.ProfileCard_company);
        company.setText(company_string);

        String department_string = typedArray.getString(R.styleable.ProfileCard_department);
        department.setText(department_string);

        typedArray.recycle();
    }


    void setName_and_Age(int name_and_age_resID) { name_and_age.setText(name_and_age_resID);
    }
    void setCompany(int company_resID) {
        company.setText(company_resID);
    }
    void setDepartment(int department_resID) { department.setText(department_resID);
    }
}
