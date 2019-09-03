package com.yamigu.yamigu_app.Fragment;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yamigu.yamigu_app.R;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WListFragment extends Fragment {
    private Toolbar tb;
    private String auth_token;
    private Button[] btn_date_list;
    private String[] date_list;
    private Set<String> active_date_set;
    private LayoutInflater mInflater;
    private View view;
    static int id = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_wlist, container, false);
        auth_token = getActivity().getIntent().getExtras().getString("auth_token");
        tb = (Toolbar) view.findViewById(R.id.toolbar) ;
        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


        btn_date_list = new Button[7];
        date_list = new String[7];
        int[] btn_date_id_list = {R.id.btn_date_1, R.id.btn_date_2, R.id.btn_date_3, R.id.btn_date_4, R.id.btn_date_5, R.id.btn_date_6, R.id.btn_date_7};
        int i = 0;
        for(TextView btn_date : btn_date_list) {
            btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
            String getTime = sdf.format(cal.getTime());
            String getTime2 = sdf2.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            date_list[i] = getTime2;
            i++;
            btn_date.setText(getTime);
        }



        String url = "http://192.168.43.223:9999/api/meetings/my/";
        ContentValues values = new ContentValues();
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_filter :
                FilterSetFragment f = FilterSetFragment.getInstance();
                f.show(super.getActivity().getSupportFragmentManager(), FilterSetFragment.TAG_DIALOG_EVENT);
                break;
        }
        return super.onOptionsItemSelected(item);
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

            result = requestHttpURLConnection.request(url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jsonArray = null;
            active_date_set = new HashSet<>();
            try {
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    active_date_set.add(jsonArray.getJSONObject(i).getString("date"));
                }
                SimpleDateFormat sdf = new SimpleDateFormat("M/d");
                int i = 0;
                int[] btn_date_id_list = {R.id.btn_date_1, R.id.btn_date_2, R.id.btn_date_3, R.id.btn_date_4, R.id.btn_date_5, R.id.btn_date_6, R.id.btn_date_7};
                String url = "http://192.168.43.223:9999/api/meetings/waiting/";
                url += "?";
                ContentValues values = new ContentValues();
                for(String active_date : active_date_set) {
                    Date from = null;
                    try {
                        from = new SimpleDateFormat("yyyy-MM-dd").parse(active_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String to = sdf.format(from);
                    for(TextView btn_date : btn_date_list) {
                        btn_date = (TextView) view.findViewById(btn_date_id_list[i]);
                        if(btn_date.getText().equals(to)) {
                            btn_date.setTextColor(view.getResources().getColor(R.color.colorPoint));
                        }
                        i++;
                    }
                    url += "date="+active_date + "&";
                }
                NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                networkTask2.execute();
            }
            catch (JSONException e) {
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

            result = requestHttpURLConnection.request(url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("MeetingList: ", s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                LinearLayout mRootLinear = (LinearLayout) view.findViewById(R.id.wating_card_root);
                int w_id = findId();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Returns a valid id that isn't in use
    public int findId(){
        View v = view.findViewById(id);
        while (v != null){
            v = view.findViewById(++id);
        }
        return id++;
    }
}
