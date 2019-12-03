package com.yamigu.yamigu_app.Activity;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class AllianceListActivity extends AppCompatActivity {
    private Toolbar tb;
    private ListView listview_alliance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alliance_list);
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
        listview_alliance = findViewById(R.id.listview_alliance);
        listview_alliance.setDivider(null);
        ArrayList<String> data_list = new ArrayList<>();
        data_list.add("안녕하세요 :)\n" +
                "미팅하기 좋은 매장을 추천해줄게요!\n" +
                "보통 룸술집에서 미팅을 많이하고 이자카야도 프라이빗한 공간이라면 미팅해도 괜찮아요!");
        data_list.add("신촌\n" +
                "- 오렌지룸 (룸술집)\n" +
                "- 꾼노리 (룸술집) \n" +
                "- 구노포차 (룸술집)");
        data_list.add("홍대\n" +
                "- 오렌지룸 (룸술집)\n" +
                "- 꾼노리 (룸술집)\n" +
                "- 노라바라 (룸술집)\n" +
                "- 원나잇 (룸술집)\n" +
                "- 372 (룸술집)\n" +
                "- 노크 (룸술집)\n" +
                "- 이자카야 유메노요코쵸 (이자카야)");
        data_list.add("건대\n" +
                "- 오렌지룸 (룸술집)\n" +
                "- 꾼노리 (룸술집)\n" +
                "- 더블유 (룸술집)\n" +
                "- 마실리아 (룸술집)\n" +
                "- 이자카야 도키도키 (이자카야)");
        data_list.add("왕십리\n" +
                "- 헤이판 (룸술집)\n" +
                "- 달포차 (룸술집)");
        data_list.add("강남\n" +
                "- 꾼노리 (룸술집)\n" +
                "- 더짝 (룸술집)\n" +
                "- 노크 (룸술집)\n" +
                "- 블랙파티 (룸술집)\n" +
                "- 홀릭스 (룸술집)\n" +
                "- 기세끼 (이자카야)");
        data_list.add("수원역\n" +
                "- 꾼노리 (룸술집)\n" +
                "- m2 (룸술집)\n");
        data_list.add("인천 송도\n" +
                "- 노상 (송도의 꽃)\n" +
                "- 블라블라");
        data_list.add("부산 서면\n" +
                "- 어썸 (룸술집)\n" +
                "- 노크 (룸술집)\n" +
                "- 또친 (룸술집)\n" +
                "- 짝 (룸술집) \n" +
                "- 하루 (룸술집) \n" +
                "- 누룩편주 (룸술집) \n" +
                "- 간지츠 (이자카야) ");
        final Adapter adapter = new Adapter(getApplicationContext(), data_list);
        listview_alliance.setAdapter(adapter);
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
    class PlaceRecommendData {

    }
    class Adapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<String> data_list;

        public Adapter(Context context, ArrayList<String> data) {
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
        public String getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.manager_chat, null);
            TextView content = view.findViewById(R.id.chatting_content);
            content.setText(data_list.get(position));

            return view;
        }
    }
}
