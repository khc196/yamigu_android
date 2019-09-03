package com.yamigu.yamigu_app.CustomLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class PlaceCard extends LinearLayout {

    TextView name, phone, location, benefit1, benefit2;
    ImageView image;


    public PlaceCard(Context context) {
        super(context);
        initView();
    }
    public PlaceCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);

    }
    public PlaceCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.place_card, this, false);
        addView(v);

        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
        location = (TextView) findViewById(R.id.location);
        benefit1 = (TextView) findViewById(R.id.benefit1);
        benefit2 = (TextView) findViewById(R.id.benefit2);
        image = (ImageView) findViewById(R.id.image);
    }
    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlaceCard);
        setTypeArray(typedArray);
    }
    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlaceCard, defStyle, 0);
        setTypeArray(typedArray);
    }
    private void setTypeArray(TypedArray typedArray) {

        String name_string = typedArray.getString(R.styleable.PlaceCard_name);
        name.setText(name_string);

        String phone_string = typedArray.getString(R.styleable.PlaceCard_phone);
        phone.setText(phone_string);

        String location_string = typedArray.getString(R.styleable.PlaceCard_location);
        location.setText(location_string);

        String benefit1_string = typedArray.getString(R.styleable.PlaceCard_benefit1);
        benefit1.setText(benefit1_string);

        String benefit2_string = typedArray.getString(R.styleable.PlaceCard_benefit2);
        benefit2.setText(benefit2_string);


        image.setImageDrawable(typedArray.getDrawable(R.styleable.PlaceCard_image));

        typedArray.recycle();
    }


    public void setName(int name_resID) { name.setText(name_resID); }
    public void setPhone(int phone_resID) { phone.setText(phone_resID);}
    public void setLocation(int location_resID) { location.setText(location_resID); }
    public void setBenefit1(int benefit1_resID) { benefit1.setText(benefit1_resID); }
    public void setBenefit2(int benefit2_resID) { benefit2.setText(benefit2_resID); }
    public void setImage(int image_resID) { image.setImageResource(image_resID); }
}
