package com.yamigu.yamigu_app.Fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.CustomLayout.WaitingTeamCard3;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;

import com.yamigu.yamigu_app.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class MeetingCardFragment extends Fragment {

    public WaitingTeamCard3 waitingTeamCard;

    public LinearLayout ll_btn_layout;
    private String auth_token;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_card, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
        //View waitingTeamCard = inflater.inflate(R.layout.meeting_team_request, mRootLinear, false);
        waitingTeamCard = view.findViewById(R.id.waiting_team_card);
        ll_btn_layout = view.findViewById(R.id.btn_layout);
        RelativeLayout rl_applying;
        ImageView point_line;
        TextView profile1, profile2, date, place, rating, label;
        WebView description;
        Button btn_left, btn_right;
        String TAG;
        final int request_id;
        if (getArguments() != null) {
            Bundle args = getArguments();
            TAG = args.getString("TAG");
            auth_token = preferences.getString("auth_token", "");
            request_id = args.getInt("request_id");
            rl_applying = (RelativeLayout) waitingTeamCard.findViewById(R.id.rl_applying);
            label = (TextView) waitingTeamCard.findViewById(R.id.label);
            point_line = (ImageView) waitingTeamCard.findViewById(R.id.point_line);
            description = (WebView) waitingTeamCard.findViewById(R.id.description);
            profile1 = (TextView) waitingTeamCard.findViewById(R.id.profile1);
            profile2 = (TextView) waitingTeamCard.findViewById(R.id.profile2);
            date = (TextView) waitingTeamCard.findViewById(R.id.date);
            place = (TextView) waitingTeamCard.findViewById(R.id.place);
            rating = (TextView) waitingTeamCard.findViewById(R.id.rating);
            CircularImageView profile_img = (CircularImageView) waitingTeamCard.findViewById(R.id.iv_profile);
            btn_left = view.findViewById(R.id.btn_left);
            btn_right = view.findViewById(R.id.btn_right);
            final MeetingCardFragment me = this;





            if(TAG.equals("sent")) {
                final SentMeetingFragment parent_s = (SentMeetingFragment) getParentFragment();
                btn_left.setText("수락 대기중");
                btn_right.setText("취소");
                btn_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                        alert_confirm.setMessage("요청을 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String url = "http://192.168.0.10:9999/api/meetings/cancel_request/";
                                        int id = request_id;
                                        ContentValues values = new ContentValues();
                                        values.put("request_id", id);
                                        NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                                        networkTask2.execute();
                                        parent_s.getActivity().getSupportFragmentManager().beginTransaction().detach(parent_s).attach(parent_s).commit();
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
            else {
                final ReceivedMeetingFragment parent_r = (ReceivedMeetingFragment) getParentFragment();
                btn_left.setText("미팅 수락");
                btn_right.setText("거절");

                btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getContext());
                        alert_confirm.setMessage("미팅을 수락하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String url = "http://192.168.0.10:9999/api/meetings/accept_request/";
                                        int id = request_id;
                                        ContentValues values = new ContentValues();
                                        values.put("request_id", id);
                                        NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                                        networkTask2.execute();
                                        final Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        getActivity().finish();
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
                                        String url = "http://192.168.0.10:9999/api/meetings/decline_request/";
                                        int id = request_id;
                                        ContentValues values = new ContentValues();
                                        values.put("request_id", id);
                                        NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                                        networkTask2.execute();
                                        parent_r.getActivity().getSupportFragmentManager().beginTransaction().detach(parent_r).attach(parent_r).commit();
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
            String desc_string, profile1_string, profile2_string, date_string, place_string, before_date_string;
            desc_string = args.getString("appeal");
            profile1_string = args.getString("nickname") + " (" + args.getInt("age") + ")";
            profile2_string = args.getString("belong") + ", " + args.getString("department");
            before_date_string = args.getString("date");
            try {
                int label_type = args.getInt("type");
                switch (label_type) {
                    case 1:
                        label.setBackgroundResource(R.drawable.label_2vs2_bg);
                        label.setText("2:2 소개팅");
                        point_line.setBackgroundColor(getResources().getColor(R.color.colorPoint));
                        rating.setTextColor(getResources().getColor(R.color.colorPoint));
                        rl_applying.setBackgroundResource(R.drawable.bottom_rounded_orange);
                        break;
                    case 2:
                        label.setBackgroundResource(R.drawable.label_3vs3_bg);
                        label.setText("3:3 미팅");
                        point_line.setBackgroundColor(getResources().getColor(R.color.color3vs3));
                        rating.setTextColor(getResources().getColor(R.color.color3vs3));
                        rl_applying.setBackgroundResource(R.drawable.bottom_rounded_3vs3);
                        break;
                    case 3:
                        label.setBackgroundResource(R.drawable.label_4vs4_bg);
                        label.setText("4:4 미팅");
                        point_line.setBackgroundColor(getResources().getColor(R.color.color4vs4));
                        rating.setTextColor(getResources().getColor(R.color.color4vs4));
                        rl_applying.setBackgroundResource(R.drawable.bottom_rounded_4vs4);
                        break;
                }

                Date before_date = new SimpleDateFormat("yyyy-MM-dd").parse(before_date_string);
                SimpleDateFormat sdf = new SimpleDateFormat("M월d일");
                date_string = sdf.format(before_date);
                place_string = args.getString("place_type_name");
                description.loadData("<div style=\"display:table; width:100%; height:100%; background-color:rgba(255,255,255, 0);overflow-y:hidden;\"><div style=\"display: table-cell; vertical-align: middle; text-align:center; word-break: break-all; color: black; font-size:12px; padding:3px;overflow-y:hidden;overflow-x:hidden;\">"+desc_string+"</div></div>", "text/html;charset=UTF-8", "UTF-8");
                profile1.setText(profile1_string);
                profile2.setText(profile2_string);
                date.setText(date_string);
                place.setText(place_string);
                String url = args.getString("profile_img_url");
                if(!url.isEmpty()) {
                    ContentValues values = new ContentValues();
                    NetworkTask networkTask = new NetworkTask(url, values, profile_img);
                    networkTask.execute();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public class NetworkTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private CircularImageView civ;
        public NetworkTask(String url, ContentValues values,  CircularImageView civ) {
            this.url = url;
            this.values = values;
            this.civ = civ;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlO = new URL(url);

                URLConnection conn = urlO.openConnection();
                conn.connect();
                InputStream urlInputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bm) {
            civ.setImageBitmap(bm);
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

