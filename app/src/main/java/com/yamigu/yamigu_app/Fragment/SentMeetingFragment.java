package com.yamigu.yamigu_app.Fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.Adapter.FragmentAdapter;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SentMeetingFragment extends Fragment {
    private FragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private TextView tv_current, tv_total;
    private boolean is_initialized = false;
    private LinearLayout page_layout, empty_layout;
    //private LinearLayout ll_btn_layout;
    //private Button btn_left, btn_right;
    private static int position;
    private String auth_token;
    private int total_num = 0;
    int dpValue = 0;
    int meeting_id;
    private SharedPreferences preferences;
    public SentMeetingFragment() {
        // Required empty public constructor
    }
    public static SentMeetingFragment newInstance() {
        SentMeetingFragment fragment = new SentMeetingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (getArguments() != null) {
            meeting_id = getArguments().getInt("meeting_id");
            auth_token = preferences.getString("auth_token", "");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_meeting, container, false);
        // Inflate the layout for this fragment

        page_layout = view.findViewById(R.id.page_view);
        empty_layout = view.findViewById(R.id.empty_view);
        //ll_btn_layout = view.findViewById(R.id.btn_layout);
        //btn_left = view.findViewById(R.id.btn_left);
        //btn_right = view.findViewById(R.id.btn_right);

        tv_current = view.findViewById(R.id.tv_num_of_sent);
        tv_total = view.findViewById(R.id.tv_total_of_sent);


        tv_total.setText(Integer.toString(3));
        tv_current.setText(Integer.toString(1));


        viewPager = view.findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    MeetingCardFragment fragment;
                    int currentPage = viewPager.getCurrentItem();
                    SentMeetingFragment.position = position;
                    tv_current.setText(Integer.toString(currentPage + 1));
                    for (int i = 1; i <= total_num; i++) {
                        float d = getResources().getDisplayMetrics().density;
                        fragment = fragmentAdapter.getItem(i - 1);
                        //LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
                        if (i - 1 == currentPage) {
                            //mLayoutParams.topMargin = 0;
                            fragment.waitingTeamCard.setAlpha(1.0f);
                        } else {
                            //mLayoutParams.topMargin = Math.round(16 * d);
                            fragment.waitingTeamCard.setAlpha(0.4f);
                        }
                        //fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                    }
                    refresh();
                } catch(NullPointerException e) {
                    refresh();
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }

        });
        //fragmentAdapter = new FragmentAdapter(getFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        ViewTreeObserver vto = viewPager.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(is_initialized) {
                    //int width = viewPager.getWidth();
                    //float d = getResources().getDisplayMetrics().density;
                    //dpValue = (int) (width / d);
                    //int margin = (int) (45 * dpValue / 411 * d);
                    //viewPager.setPadding(margin, 0, margin, 0);
                    MeetingCardFragment fragment = fragmentAdapter.getItem(0);
                    //LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
                    //mLayoutParams.topMargin = 0;
                    if(SentMeetingFragment.position == 0) {
                        fragment.waitingTeamCard.setAlpha(1.0f);
                        //fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                    }

                }
            }
        });
        viewPager.setClipToPadding(false);

        String url = "http://106.10.39.154:9999/api/matching/sent_request/?meeting_id="+meeting_id;
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
        final Fragment me = this;
        /*
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                alert_confirm.setMessage("요청을 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "http://106.10.39.154:9999/api/matching/cancel_request/";
                                MeetingCardFragment fragment = fragmentAdapter.getItem(viewPager.getCurrentItem());

                                int id = fragment.getRequest_id();
                                ContentValues values = new ContentValues();
                                values.put("request_id", id);
                                NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                                networkTask2.execute();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(me).attach(me).commit();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

         */
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void refresh() {
        if(fragmentAdapter != null) {
            fragmentAdapter.notifyDataSetChanged();
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(s);
                total_num = jsonArray.length();
                tv_total.setText(Integer.toString(total_num));
                for(int i = 0; i < jsonArray.length(); i++) {
                    int request_id = jsonArray.getJSONObject(i).getInt("id");
                    JSONObject json_data = jsonArray.getJSONObject(i).getJSONObject("receiver");
                    //Log.d("onPostExecute", json_data.toString());

                    MeetingCardFragment meetingCardFragment = new MeetingCardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", "sent");
                    bundle.putInt("request_id", request_id);
                    bundle.putInt("type", json_data.getInt("meeting_type"));
                    bundle.putString("nickname", json_data.getString("openby_nickname"));
                    bundle.putInt("age", json_data.getInt("openby_age"));
                    bundle.putString("belong", json_data.getString("openby_belong"));
                    bundle.putString("department", json_data.getString("openby_department"));
                    bundle.putString("date", json_data.getString("date"));
                    bundle.putString("appeal", json_data.getString("appeal"));
                    bundle.putString("place_type_name", json_data.getString("place_type_name"));
                    bundle.putString("profile_img_url", json_data.getString("openby_profile"));
                    meetingCardFragment.setArguments(bundle);
                    fragmentAdapter.addItem(meetingCardFragment);
                }
                refresh();
                if(jsonArray.length() > 0) {
                    is_initialized = true;
                    page_layout.setVisibility(View.VISIBLE);
                    empty_layout.setVisibility(View.INVISIBLE);
                    //ll_btn_layout.setVisibility(View.VISIBLE);
                }
                else {
                    page_layout.setVisibility(View.INVISIBLE);
                    empty_layout.setVisibility(View.VISIBLE);
                    //ll_btn_layout.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;

        }
    }
}
