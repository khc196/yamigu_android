package com.yamigu.yamigu_app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yamigu.yamigu_app.R;

public class TicketActivity extends AppCompatActivity {
    private Toolbar tb;
    private LinearLayout ticket_1, ticket_2;
    private Button btn_pay;
    private Selector selector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
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
        ticket_1 = (LinearLayout) findViewById(R.id.ticket_1);
        ticket_2 = (LinearLayout) findViewById(R.id.ticket_2);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        selector = new Selector();
        ticket_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleFirst();
                ticket_1.setBackgroundResource(selector.isFirst() ? R.drawable.shadow_box_orange : R.drawable.shadow_box);
                ticket_2.setBackgroundResource(selector.isSecond() ? R.drawable.shadow_box_orange : R.drawable.shadow_box);
            }
        });
        ticket_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleSecond();
                ticket_1.setBackgroundResource(selector.isFirst() ? R.drawable.shadow_box_orange : R.drawable.shadow_box);
                ticket_2.setBackgroundResource(selector.isSecond() ? R.drawable.shadow_box_orange : R.drawable.shadow_box);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    private class Selector {
        private boolean first, second;
        public Selector() {
            first = false;
            second = false;
        }

        public boolean isFirst() {
            return first;
        }
        public boolean isSecond() {
            return second;
        }
        public void toggleFirst() {
            first = !first;
            if(first) second = false;
        }
        public void toggleSecond() {
            second = !second;
            if(second) first = false;
        }
    }
}
