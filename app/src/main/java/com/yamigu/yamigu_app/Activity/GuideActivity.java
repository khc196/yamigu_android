package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

public class GuideActivity extends AppCompatActivity {
    private Toolbar tb;
    private LinearLayout root_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
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
        root_view = findViewById(R.id.root_view);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.manager_chat,  root_view, false);
        ((TextView)view.findViewById(R.id.chatting_content)).setText("안녕하세요 :)\n" +
                "야미구 가입해줘서 고마워요!\n" +
                "야미구로 새로운 이성친구 만드는 방법 알려드릴게요!  \n" +
                "\n" +
                "1. 티켓 하나당 하나의 미팅 카드를 신청할 수 있어요.\n" +
                "단, 티켓은 매칭된 이후에 소멸되니 걱정하지 마세요!\n" +
                "\n" +
                "2. 미팅을 같이 나갈 친구들을 구해 미팅을 신청하세요! 신청은 한명만 해도 이루어집니다.\n" +
                "\n" +
                "3. 일주일 이내에 친구와 가능한 날짜를 선택하세요!\n" +
                "\n" +
                "4. 원하는 지역을 선택하세요! \n" +
                "\n" +
                "5. 이성에게 자신들의 매력을 어필하거나 원하는 이성을 간단한 글로 설명하세요!\n" +
                "\n" +
                "6. 이제 야미구를 신청하셨어요! 대기팀에 가서 원하는 조건의 이성이 있는지 살펴보세요. 원하는 이성이 있으면 신청하세요!\n" +
                "\n" +
                "7. 이성에게 신청을 받으면 수락 또는 거절을 해주세요! 수락을 누를시 신청카드는 매칭카드로 바뀌게 됩니다!\n" +
                "\n" +
                "8. 매칭이 되면 야미구 매니저와 이성과의 3인 채팅방이 생겨요. 약속을 잡고 만나기까지 야미구에서 모두 해결하세요! 궁금한 점이 있으면 바로바로 매니저에게 물어봐주세요.");
        root_view.addView(view);
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
}
