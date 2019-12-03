package com.yamigu.yamigu_app.Fragment;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class SafeMeetingFragment extends Fragment {
    private ListView listview_safe_meeting;
    ArrayList<SafeMeetingData> data_list;
    public SafeMeetingFragment() {
        // Required empty public constructor
        data_list = new ArrayList<>();
        data_list.add(new SafeMeetingData(R.drawable.safe_meeting_1, "이중인증", "본인인증 + 소속인증을 통해 신분이 보장된 사람들과 만나세요."));
        data_list.add(new SafeMeetingData(R.drawable.safe_meeting_2, "야미구 매니저", "미팅 약속은 야미구 매니저와 함께 정하세요. 매니저가 관리해줄게요."));
        data_list.add(new SafeMeetingData(R.drawable.safe_meeting_3, "제휴매장(추가 예정)", "야미구와 제휴를 맺은 프리미엄 매장에서 안전하게 미팅하세요."));
        data_list.add(new SafeMeetingData(R.drawable.safe_meeting_4, "개인정보보호", "야미구에서는 사진 등 민감한 정보는 필요없어요. 개성있는 글로 표현하세요."));
    }
    public static SafeMeetingFragment newInstance(String param1, String param2) {
        SafeMeetingFragment fragment = new SafeMeetingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safe_meeting, container, false);
        listview_safe_meeting = view.findViewById(R.id.listview_safe_meeting);

        final Adapter adapter = new Adapter(getContext(), data_list);
        listview_safe_meeting.setAdapter(adapter);
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

    class SafeMeetingData {
        int image;
        String title;
        String content;
        SafeMeetingData(int image, String title, String content) {
            this.image = image;
            this.title = title;
            this.content = content;
        }
    }
    class Adapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<SafeMeetingData> data_list;

        public Adapter(Context context, ArrayList<SafeMeetingData> data) {
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
        public SafeMeetingData getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.safe_meeting, null);
            ImageView iv = view.findViewById(R.id.image);
            TextView title = view.findViewById(R.id.title);
            TextView content = view.findViewById(R.id.content);
            iv.setImageResource(data_list.get(position).image);
            title.setText(data_list.get(position).title);
            content.setText(data_list.get(position).content);
            return view;
        }
    }

}
