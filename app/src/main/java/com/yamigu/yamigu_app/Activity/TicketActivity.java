package com.yamigu.yamigu_app.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.yamigu.yamigu_app.Fragment.HomeFragment;
import com.yamigu.yamigu_app.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TicketActivity extends AppCompatActivity {
    private Toolbar tb;
    private LinearLayout ticket_1, ticket_2;
    private TextView tv_before_price1, tv_before_price2;
    private BillingClient billingClient;
    private ConsumeResponseListener consumeResponseListener;
    private Selector selector;
    private List<SkuDetails> mSkuDetailList;
    private Activity mAcitivty;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int num_of_ticket;
    private static int buying_ticket = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        tv_before_price1 = findViewById(R.id.before_price1);
        tv_before_price2 = findViewById(R.id.before_price2);
        tv_before_price1.setPaintFlags(tv_before_price1.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        tv_before_price2.setPaintFlags(tv_before_price2.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        auth_token = preferences.getString("auth_token", "");
        num_of_ticket = preferences.getInt("num_of_ticket", 0);
        selector = new Selector();
        ticket_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleFirst();

                if(selector.isFirst()) {
                    purchaseTicket(0);
                }
            }
        });
        ticket_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selector.toggleSecond();

                if(selector.isSecond()) {
                    purchaseTicket(1);
                }
            }
        });
        mAcitivty = this;
        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
        consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingClient","상품을 성공적으로 소모하였습니다");
                }
                else {
                    Log.d("BillingClient", "상품 소모에 실패하였습니다. 오류코드: "+ billingResult.getResponseCode() + ":" + billingResult.getDebugMessage());
                }
            }
        };
        billingClient = BillingClient.newBuilder(TicketActivity.this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    Log.d("BillingClient", "결제에 성공하였습니다");
                    for(Purchase pur: purchases){
                        Log.d("BillingClient", pur.getSignature());
                        ConsumeParams consumeParams =
                                ConsumeParams.newBuilder()
                                        .setPurchaseToken(pur.getPurchaseToken())
                                        .setDeveloperPayload(pur.getDeveloperPayload())
                                        .build();
                        if(pur.getSku().equals("ticket_1")) {
                            if(sendTicketToAPI(1, pur.getPurchaseToken()) == 0)
                                billingClient.consumeAsync(consumeParams, consumeResponseListener);
                        }
                        else if(pur.getSku().equals("ticket_2_plus_1")) {
                            if(sendTicketToAPI(3, pur.getPurchaseToken()) == 0)
                                billingClient.consumeAsync(consumeParams, consumeResponseListener);
                        }
                    }
                    //sendTicketToAPI(ticket_count);
                }
                else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Log.d("BillingClient", "사용자에 의해 결제가 취소되었습니다");
                }
                else {
                    Log.d("BillingClient", "결제가 취소되었습니다");
                }
                if(selector.isFirst()) selector.toggleFirst();
                else if(selector.isSecond()) selector.toggleSecond();
            }
        }).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("BillingClient", "구글 결제 서버에 접속 성공했습니다");
                    final List<String> skuList = new ArrayList<>();
                    skuList.add("ticket_1");
                    skuList.add("ticket_2_plus_1");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    //Log.d("SKUDetailsList", skuDetailsList.toString());
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                        Log.d("BillingClient", "응답 받은 데이터 숫자: "+ skuDetailsList.size());
                                        for(int i = 0; i < skuDetailsList.size(); i++) {
                                            Log.d("BillingClient", skuDetailsList.get(i).getTitle() + ": " + skuDetailsList.get(i).getPrice());
                                        }
                                        mSkuDetailList = skuDetailsList;
                                    }
                                    else {
                                        Log.d("BillingClient", "상품 정보를 가져오던 중 오류가 발생했습니다. 오류코드: "+ billingResult.getResponseCode());
                                    }
                                }
                            });
                }
                else {
                    Log.d("BillingClient", "구글 결제 서버에 접속 실패했습니다");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("BillingClient", "구글 결제 서버 접속이 끊어졌습니다");
            }
        });

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    public void purchaseTicket(int index) {
        SkuDetails skuDetails = mSkuDetailList.get(index);
        Log.d("BillingClient", skuDetails.getTitle());
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(mAcitivty, flowParams);
        //if(billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
        //}
        //Log.d("BillingResult", billingResult.getResponseCode()+":"+billingResult.getDebugMessage());
    }
    public int sendTicketToAPI(final int num, final String purchase_token) {
        int serverResponseCode = -1;
        final Dialog dialog = ProgressDialog.show(this, "", "티켓을 구매중입니다...", true);
        try {
            HttpURLConnection conn = null;
            String Uri = "http://106.10.39.154:9999/api/buyticket/";
            URL url = new URL(Uri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setRequestProperty("Authorization", "Token " + auth_token);

            StringBuffer sbParams = new StringBuffer();
            sbParams.append("device").append("=").append("android").append("&");
            sbParams.append("purchase_token").append("=").append(purchase_token).append("&");
            sbParams.append("purchase_number").append("=").append(num);
            String strParams = sbParams.toString();
            OutputStream os = conn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("BillingClient", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "티켓 구매에 성공했습니다",
                                Toast.LENGTH_SHORT).show();
                        editor.putInt("num_of_ticket", num_of_ticket + num);
                        editor.apply();
                        HomeFragment.ticket_count = num_of_ticket + num;
                    }
                });
                dialog.dismiss();
                return 0;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
        return -1;
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
            ticket_1.setBackgroundResource(selector.isFirst() ? R.drawable.ticket_bg_orange : R.drawable.ticket_bg);
            ticket_2.setBackgroundResource(selector.isSecond() ? R.drawable.ticket_bg_orange : R.drawable.ticket_bg);
        }
        public void toggleSecond() {
            second = !second;
            if(second) first = false;
            ticket_1.setBackgroundResource(selector.isFirst() ? R.drawable.ticket_bg_orange : R.drawable.ticket_bg);
            ticket_2.setBackgroundResource(selector.isSecond() ? R.drawable.ticket_bg_orange : R.drawable.ticket_bg);
        }
    }
}
