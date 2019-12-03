package com.yamigu.yamigu_app.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yamigu.yamigu_app.R;

import java.util.ArrayList;

public class WhatisYamiguFragment extends Fragment {
    private ListView listview_whatis_yamigu;
    ArrayList<WhatisYamiguData> data_list;

    public WhatisYamiguFragment() {
        data_list = new ArrayList<>();
        data_list.add(new WhatisYamiguData(R.drawable.what_is_yamigu_1));
        data_list.add(new WhatisYamiguData(R.drawable.what_is_yamigu_2));
        data_list.add(new WhatisYamiguData(R.drawable.what_is_yamigu_3));
        // Required empty public constructor
    }

    public static WhatisYamiguFragment newInstance(String param1, String param2) {
        WhatisYamiguFragment fragment = new WhatisYamiguFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whatis_yamigu, container, false);
        listview_whatis_yamigu = view.findViewById(R.id.listview_whatisyamigu);

        final Adapter adapter = new Adapter(getContext(), data_list);
        listview_whatis_yamigu.setAdapter(adapter);
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

    class WhatisYamiguData {
        int image;
        WhatisYamiguData(int image) {
            this.image = image;
        }
    }
    class Adapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<WhatisYamiguData> data_list;

        public Adapter(Context context, ArrayList<WhatisYamiguData> data) {
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
        public WhatisYamiguData getItem(int position) {
            return data_list.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.whatisyamigu, null);
            ImageView iv = view.findViewById(R.id.image);
            iv.setImageResource(data_list.get(position).image);
            return view;
        }
    }

}
