package com.yamigu.yamigu_app.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {
    private ListView listview_review;
    ArrayList<ReviewData> data_list;
    public ReviewFragment() {
        // Required empty public constructor
        data_list = new ArrayList<>();
        data_list.add(new ReviewData("이OO(22) 성균관대 컴퓨터교육학과", "페이스북으로 우연히 신청하게 되었습니다. 재미있어 보였고 해보고 싶었던 미팅이기에 신청했습니다. 매칭속도가 엄청 빨랐습니다. 다른것보다, 주선자 친구가 없으니 조금 더 편하게 만날 수 있었던것 같습니다!"));
        data_list.add(new ReviewData("이OO(21) 이화여대 경영학과", "일단 재미있는 미팅하게 해주셔서 감사해요! 날짜가 픽스되어있으니까 확실히 일정조정이라는 번거로운 과정이 빠져서 되게 편했던것 같습니다. 확실히 그 날 시간이 되는 분들만 모으면 되니까 처음에 방 만들어 일정 얘기하다가 서로 너무 안맞으니 곤란해서 파토가 나거나 인원이 계속 바뀌는 문제가 종종 있었어서요."));
        data_list.add(new ReviewData("박OO(24) 서강대 경영학과", "새내기때는 친구들 주선으로 미팅을 많이 할 수 있었는데 군대 전역을 하고 나니 미팅을 하고 싶어도 기회를 갖기 어려웠습니다. 그런데 이번 기회를 통해 쉽게 매칭 될 수 있어서 좋았습니다. 보통 장소나 시간 정하는데도 남녀 양쪽에서 시간을 많이 쏟고 어려움이 있는데 간편하게 할 수 있어서 매우 편리했습니다."));
        data_list.add(new ReviewData("이OO(21) 이화여대 행정학과", "적은 조건대로 딱딱 맞춰서 바로 연락주셔서 너무 감사했어요. 무엇보다 신분보장, 약속일정의 확실함, 파토날 위험성이 적은 것 등이 제일 좋았습니다. 그리고 절차 하나하나 다 자세하고 친절하게 해주셔서 신뢰감 200%였어요!"));
        data_list.add(new ReviewData("유OO(22) 연세대 도시공학과", "미팅을 신청하는 과정이 매우 간단하고 원하는 날짜에 잡을 수 있어서 좋았습니다. 또한 미리 날짜와 장소가 잡혀서 평소의 미팅 파토를 예방할 수 있었습니다."));
        data_list.add(new ReviewData("최OO(22) 연세대 간호학과", "우선 한번 해볼까라는 생각으로 신청하게 되었는데! 학생증하고 신분증으로 개인확인도 하고, 또 주선해주신분하고 같이 있는 단톡이 있어서 믿음이 많이 갔어요! 그리고 시간하고 장소 고를 수 있는 점도 좋았어요! 미팅하면 시간하고 장소 고르기가 항상 애매하고 힘들었는데 제가 원하는 조건을 선택할 수 있어서 편했습니다!! "));
        data_list.add(new ReviewData("박OO(24) 서강대 화학생명공학과", "일단은 학번이 학번인지라 미팅이 이제 거의 없는 시점인데 미팅이 생겨서 좋았습니다. 페이스북으로 간단한 폼 작성을 통해 제가 선택한 날짜에 미팅이 매칭됩니다. 상대방과 따로 날짜를 맞추는 일 없이 편리하게 진행이 됩니다. 날짜를 맞추기 위해 힘들게 투표하고 그럴 이유가 없습니다!!"));
        data_list.add(new ReviewData("한OO(20) 서강대 게임그래픽애니과", "저희과는 미팅이나 과팅이 잘 안들어오는 과라 아쉬웠었는데 이렇게 쉽게 신청하고 매칭될 수 있다는 점이 좋았어요! 미팅에서 만난 분들도 다 좋은 분들에 미팅 분위기도 괜찮았구요! 다음에 기회가 되면 친구들이랑 또 같이 신청해서 놀고싶네요~! "));
        data_list.add(new ReviewData("박OO(24) 연세대 전기전자공학부", "매칭과정이 너무 간단하고 간편했어요! 애들이랑 술마시다가 미팅하자고 해서 신청만 했는데 이틀후에 바로 연락이와서 놀랐어요. 또 보통 날짜잡는게 가장 어려운데 애초에 시간이 맞는 분들과 매칭이 되어서 너무 편한 것 같아요."));
        data_list.add(new ReviewData("차OO(21) 이화여대 영어교육학과", "일단 결론부터 말하자면 너무 좋았어요!! 일학년때 인맥을 다 써서 이학년 올라오면 미팅 어떻게 구하나 고민했었는데 이렇게 쉽고 간편하게 미팅을 구할 수 있어서 좋았어요! 미팅 매칭하는 과정도 빨리 이루어져서 답답하지도 않았고, 이렇게 하니 일정도 맞춰야하는 번거로움이 줄어서 편했어요! 정말 감사합니다 어제 미팅 하신 분들도 다들 좋으셨고 즐거운 미팅이었습니다. 미팅하다보면 예의없는 사람들도 많고, 익명이라는 이유로 쉽게 파토내기도 하는데 신분을 밝히고 하는거라서 그런지 더더욱 그럴 걱정할 필요도 없었습니다! "));
    }
    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        listview_review = view.findViewById(R.id.listview_review);
        listview_review.setDivider(null);



        final Adapter adapter = new Adapter(getContext(), data_list);
        listview_review.setAdapter(adapter);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    class ReviewData {
        String profile;
        String review;
        ReviewData(String profile, String review) {
            this.profile = profile;
            this.review = review;
        }
    }
    class Adapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<ReviewData> data_list;
        private ViewHolder mViewHolder;

        public Adapter(Context context, ArrayList<ReviewData> data) {
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
        public ReviewData getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.review, parent, false);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            if(position % 2 == 0) {
                mViewHolder.bg.setBackgroundResource(R.drawable.rounded_skyblue);
                mViewHolder.title.setGravity(Gravity.LEFT);
                mViewHolder.title.setTextColor(getResources().getColor(R.color.colorManb));
                mViewHolder.content.setTextColor(getResources().getColor(R.color.colorManb));
            }
            else {
                mViewHolder.bg.setBackgroundResource(R.drawable.rounded_pink);
                mViewHolder.title.setGravity(Gravity.RIGHT);
                mViewHolder.title.setTextColor(getResources().getColor(R.color.colorWomanb));
                mViewHolder.content.setTextColor(getResources().getColor(R.color.colorWomanb));
            }
            mViewHolder.title.setText(data_list.get(position).profile);
            mViewHolder.content.setText(data_list.get(position).review);
            return convertView;
        }
    }
    public class ViewHolder {
        TextView title;
        TextView content;
        LinearLayout bg;
        public ViewHolder(View convertView) {
            title = convertView.findViewById(R.id.question);
            content = convertView.findViewById(R.id.answer);
            bg = convertView.findViewById(R.id.chatting_box);
        }
    }
}
