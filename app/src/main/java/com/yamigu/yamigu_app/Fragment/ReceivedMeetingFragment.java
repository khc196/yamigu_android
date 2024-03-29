package com.yamigu.yamigu_app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.CustomLayout.CustomDialog;
import com.yamigu.yamigu_app.CustomLayout.CustomDialog2;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.Adapter.FragmentAdapter;
import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ReceivedMeetingFragment extends Fragment {
    private FragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private RelativeLayout rl_num_display;
    private TextView tv_current, tv_total;
    private boolean is_initialized = false;
    private LinearLayout page_layout, empty_layout;
    private LinearLayout ll_btn_layout;
    private Button btn_left, btn_right;
    private String auth_token;
    private static int position = 0;
    private int total_num = 0;
    int dpValue = 0;
    int meeting_id;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CustomDialog customDialog;
    private CustomDialog2 popupDialog;
    Fragment me;
    public static ProgressDialog progressDialog = null;
    public ReceivedMeetingFragment() {
        // Required empty public constructor
    }
    public static ReceivedMeetingFragment newInstance(String param1, String param2) {
        ReceivedMeetingFragment fragment = new ReceivedMeetingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private void Dialog(String title, String content1, String content2, String content3) {
        customDialog = new CustomDialog(getContext(), title, content1, content2, content3);
        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(true);

        customDialog.getWindow().setGravity(Gravity.CENTER);
        customDialog.show();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        if (getArguments() != null) {
             meeting_id = getArguments().getInt("meeting_id");
             auth_token = preferences.getString("auth_token", "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_meeting, container, false);
        progressDialog = ProgressDialog.show(getContext(), "", "로딩중입니다...", true);
        rl_num_display = view.findViewById(R.id.rl_num_display);
        rl_num_display.setVisibility(View.INVISIBLE);
        // Inflate the layout for this fragment
        page_layout = view.findViewById(R.id.page_view);
        empty_layout = view.findViewById(R.id.empty_view);
        ll_btn_layout = view.findViewById(R.id.btn_layout);
        btn_left = view.findViewById(R.id.btn_left);
        btn_right = view.findViewById(R.id.btn_right);
        tv_current = view.findViewById(R.id.tv_num_of_recv);
        tv_total = view.findViewById(R.id.tv_total_of_recv);


        tv_total.setText(Integer.toString(3));
        tv_current.setText(Integer.toString(1));

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                MeetingCardFragment fragment;
                ReceivedMeetingFragment.position = position;
                int currentPage = viewPager.getCurrentItem();
                tv_current.setText(Integer.toString(currentPage + 1));
                for(int i = 1; i <= total_num; i++) {
                    float d = getResources().getDisplayMetrics().density;
                    fragment = fragmentAdapter.getItem(i - 1);
                    //LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
                    if(i - 1 == currentPage) {
                        //mLayoutParams.topMargin = 0;
                        fragment.waitingTeamCard.setAlpha(1.0f);
                    }
                    else {
                        //mLayoutParams.topMargin = Math.round(16 * d);
                        fragment.waitingTeamCard.setAlpha(0.4f);
                    }
                    //fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                }
                refresh();
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
//                    int width = viewPager.getWidth();
//                    float d = getResources().getDisplayMetrics().density;
//                    dpValue = (int) (width / d);
//                    int margin = (int) (45 * dpValue / 411 * d);
//                    viewPager.setPadding(margin, 0, margin, 0);
                    MeetingCardFragment fragment = fragmentAdapter.getItem(0);
//                    LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) fragment.waitingTeamCard.getLayoutParams();
//                    mLayoutParams.topMargin = 0;
                    if(ReceivedMeetingFragment.position == 0) {
                        fragment.waitingTeamCard.setAlpha(1.0f);
//                        fragment.waitingTeamCard.setLayoutParams(mLayoutParams);
                    }

                }
            }
        });
        me = this;
        viewPager.setClipToPadding(false);
        String url = "http://106.10.39.154:9999/api/matching/received_request/?meeting_id="+meeting_id;
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();





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
    private void setDialog(String text) {
        popupDialog = new CustomDialog2(getContext());
        popupDialog.setCancelable(true);
        popupDialog.setCanceledOnTouchOutside(true);
        popupDialog.text = text;
        popupDialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void showDialog() {
        popupDialog.show();
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
            progressDialog.setCancelable(true);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                jsonArray = new JSONArray(s);
                total_num = jsonArray.length();
                tv_total.setText(Integer.toString(total_num));
                for(int i = 0; i < jsonArray.length(); i++) {
                    int request_id = jsonArray.getJSONObject(i).getInt("id");
                    JSONObject json_data = jsonArray.getJSONObject(i).getJSONObject("sender");
                    //Log.d("onPostExecute", json_data.toString());

                    MeetingCardFragment meetingCardFragment = new MeetingCardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", "received");
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
                if(jsonArray.length() > 0) {
                    is_initialized = true;
                    rl_num_display.setVisibility(View.VISIBLE);
                    page_layout.setVisibility(View.VISIBLE);
                    empty_layout.setVisibility(View.INVISIBLE);
                    ll_btn_layout.setVisibility(View.VISIBLE);
                }
                else {
                    rl_num_display.setVisibility(View.INVISIBLE);
                    page_layout.setVisibility(View.INVISIBLE);
                    empty_layout.setVisibility(View.VISIBLE);
                    ll_btn_layout.setVisibility(View.INVISIBLE);

                }
                refresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                    alert_confirm.setMessage("미팅을 수락하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog = ProgressDialog.show(getContext(), "", "미팅 매칭중입니다...", true);
                                    String url = "http://106.10.39.154:9999/api/matching/accept_request/";
                                    MeetingCardFragment fragment = fragmentAdapter.getItem(viewPager.getCurrentItem());

                                    int id = fragment.getRequest_id();
                                    ContentValues values = new ContentValues();
                                    values.put("request_id", id);
                                    NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                                    networkTask2.execute();
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
            btn_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                    alert_confirm.setMessage("미팅을 거절하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //progressDialog = ProgressDialog.show(getContext(), "", "미팅 거절중입니다...", true);
                                    String url = "http://106.10.39.154:9999/api/matching/decline_request/";
                                    MeetingCardFragment fragment = fragmentAdapter.getItem(viewPager.getCurrentItem());

                                    int id = fragment.getRequest_id();
                                    ContentValues values = new ContentValues();
                                    values.put("request_id", id);
                                    NetworkTask3 networkTask3 = new NetworkTask3(url, values);
                                    networkTask3.execute();
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
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    getActivity().finish();
                }
            };
            Handler mHandler = new Handler();
            progressDialog.setCancelable(true);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            super.onPostExecute(s);
            JSONObject jsonObject = null;
            String user_name = preferences.getString("nickname", "");
            try {
                jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                if(message.equals("Accepted")) {
                    int count_meeting = jsonObject.getInt("count_meeting");
                    Dialog("매칭 완료!", user_name + " 님,", "야미구에서의 " + count_meeting + "번째 만남이", "좋은 인연으로 이어지길 바랍니다!");
                    mHandler.postDelayed(mRunnable, 2000);
                    customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            final Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }
                else if(message.equals("Duplicated")) {
                    setDialog("이미 상대방 또는 내 미팅이 매칭되었어요.");
                    showDialog();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class NetworkTask3 extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask3(String url, ContentValues values) {
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
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            JSONArray jsonArray = null;
            Toast.makeText(getContext(), "미팅을 거절했어요!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
