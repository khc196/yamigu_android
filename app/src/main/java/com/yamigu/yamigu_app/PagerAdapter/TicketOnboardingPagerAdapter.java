package com.yamigu.yamigu_app.PagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamigu.yamigu_app.CustomLayout.TicketOnboardingItem;
import com.yamigu.yamigu_app.R;

import java.util.List;

public class TicketOnboardingPagerAdapter extends PagerAdapter {
    Context mContext;
    List<TicketOnboardingItem> mListItem;

    public TicketOnboardingPagerAdapter(Context mContext, List<TicketOnboardingItem> mListItem) {
        this.mContext = mContext;
        this.mListItem = mListItem;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.onboarding_screen_ticket, null);
        ImageView imgSlide = layoutScreen.findViewById(R.id.onboarding_img);
        TextView title1 = layoutScreen.findViewById(R.id.onboarding_title1);
        TextView title2 = layoutScreen.findViewById(R.id.onboarding_title2);
        TextView desc1 = layoutScreen.findViewById(R.id.onboarding_desc1);
        TextView desc2 = layoutScreen.findViewById(R.id.onboarding_desc2);

        title1.setText(mListItem.get(position).getTitle1());
        title2.setText(mListItem.get(position).getTitle2());
        desc1.setText(mListItem.get(position).getDescription1());
        desc2.setText(mListItem.get(position).getDescription2());
        imgSlide.setImageResource(mListItem.get(position).getOnboardingImg());

        container.addView(layoutScreen);

        return layoutScreen;
    }
    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
