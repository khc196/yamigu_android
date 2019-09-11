package com.yamigu.yamigu_app.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yamigu.yamigu_app.CustomLayout.WaitingTeamCard;
import com.yamigu.yamigu_app.CustomLayout.WaitingTeamCard2;
import com.yamigu.yamigu_app.R;

public class MeetingCardFragment extends Fragment {
    public WaitingTeamCard2 waitingTeamCard;
    public LinearLayout ll_btn_layout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_card, container, false);

        waitingTeamCard = view.findViewById(R.id.wating_team_card);
        ll_btn_layout = view.findViewById(R.id.btn_layout);
        if (getArguments() != null) {
            Bundle args = getArguments();
        }

        return view;
    }
}