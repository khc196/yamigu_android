package com.yamigu.yamigu_app.Activity;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CertificationWActivity extends AppCompatActivity {
    private Toolbar tb;
    private ImageButton btn_attach_file;
    private Button btn_go_home;
    private TextView btn_skip;
    private final int REQ_CODE_SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_w);
        tb = (Toolbar) findViewById(R.id.toolbar) ;
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
        btn_attach_file = (ImageButton) findViewById(R.id.btn_attach_file);
        btn_go_home = (Button) findViewById(R.id.btn_gohome);
        btn_attach_file.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){  // 클릭하면 ACTION_PICK 연결로 기본 갤러리를 불러옵니다.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        btn_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
            }
        });
        btn_skip = (TextView) findViewById(R.id.btn_skip);
        btn_skip.setPaintFlags(btn_skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.selected_img);
                    image.setVisibility(View.VISIBLE);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
}
