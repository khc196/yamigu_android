package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class FAQActivity extends AppCompatActivity {
    private Toolbar tb;
    private ListView listview_faq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_faq);
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
        listview_faq = findViewById(R.id.listview_faq);
        listview_faq.setDivider(null);
        ArrayList<FAQData> data_list = new ArrayList<>();
        data_list.add(new FAQData("야미구 미팅은 어떻게 진행되나요?", "야미구 미팅은 우선은 함께 나갈 친구와 상의하여 원하는 인원, 날짜, 장소에 맞는 이성팀과의 매칭을 우선으로 하고 있습니다. 미팅 카드 신청 후에 같은 조건에 이성팀과 매칭 후 관리자와 함께 채팅을 통해 약속을 잡고 정해진 날에 미팅을 진행하시면 됩니다 :) "));
        data_list.add(new FAQData("미팅을 같이 할 친구가 없어도 괜찮아요?", "현재 야미구 미팅은 꼭 친구와 함께 나와야합니다! 적어도 1명 이상의 친구와 함께 원하는 날짜를 얘기한 뒤 대표로 1명만 신청해야합니다! 추후에 업데이트 될 야미구 주최 미팅에서는 모르는 사람들과의 모임을 기획하고 있습니다! "));
        data_list.add(new FAQData("매칭 채팅은 왜 매니저와 함께 진행되나요?", "야미구에서 타서비스와 다른 기능중 하나가 매칭 매니저의 존재입니다! 매니저와 함께 약속 날짜와 장소, 시간을 함께 공유하여 더 안전한 만남을 지향하고 있습니다. 또한 매니저는 미팅 Tip 등 사용자와의 원활한 소통을 위해서 존재하고 있습니다."));
        data_list.add(new FAQData("인증에 계속 실패하는데 해결책이 있나요?", "학생증이나 명함 등을 첨부를 잘 해서 한번 더 부탁해요! 또한 본인인증에 나와있는 이름과 동일한 이름만이 첨부파일로 가능해요! 그래도 인증에 계속 실패하면 야미구와 1:1 대화하기에서 바로 알려주세요! "));
        data_list.add(new FAQData("소속을 변경하고 싶어요.", "소속을 변경하기 위해서는 야미구와 1:1 대화하기에서 신청하시면 됩니다!"));
        //data_list.add(new FAQData("제휴매장에서 쿠폰은 어떻게 지급되나요?", "야미구는 제휴매장에서 미팅하면 약 10,000원 상당의 제휴쿠폰을 드리고 있습니다. 미팅 당일 날 채팅방에서 지급이 되고 매장 직원에게 말하면 직원확인 버튼을 눌러서 사용 가능합니다! 단, 직원이 아닌 사용자가 확인을 누를시 쿠폰을 사용하지 못할 수도 있어요 :("));
        data_list.add(new FAQData("대기팀에 미팅카드가 없어요! 오류인가요?", "대기팀에 미팅 카드가 없는 경우는 필터를 확인하시거나 선택 날짜를 확인해보세요! 그래도 없을 경우에는 현재 미팅 대기 이성이 존재하지 않는 경우입니다! 그래도 오류가 의심되는 경우는 알려주시면 고마워요 :)"));


        final Adapter adapter = new Adapter(getApplicationContext(), data_list);
        listview_faq.setAdapter(adapter);
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
    class FAQData {
        String question;
        String answer;
        FAQData(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
    class Adapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<FAQData> data_list;

        public Adapter(Context context, ArrayList<FAQData> data) {
            mContext = context;
            data_list = data;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return data_list.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public FAQData getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.faq, null);
            TextView title = view.findViewById(R.id.question);
            TextView content = view.findViewById(R.id.answer);
            if(position % 2 == 1) {
                title.setGravity(Gravity.RIGHT);
            }
            title.setText(data_list.get(position).question);
            content.setText(data_list.get(position).answer);
            return view;
        }
    }
}
