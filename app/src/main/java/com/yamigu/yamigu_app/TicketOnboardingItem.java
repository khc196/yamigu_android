package com.yamigu.yamigu_app;

public class TicketOnboardingItem {
    String Title1, Title2, Description1, Description2;
    int OnboardingImg;

    public TicketOnboardingItem(String title1, String title2, String description1, String description2, int onboardingImg) {
        Title1 = title1;
        Title2 = title2;
        Description1 = description1;
        Description2 = description2;
        OnboardingImg = onboardingImg;
    }
    public void setTitle1(String title1) {
        Title1 = title1;
    }
    public void setTitle2(String title2) {
        Title2 = title2;
    }
    public void setDescription1(String description1) {
        Description1 = description1;
    }
    public void setDescription2(String description2) {
        Description2 = description2;
    }
    public void setOnboardingImg(int onboardingImg) {
        OnboardingImg = onboardingImg;
    }
    public String getTitle1() {
        return Title1;
    }
    public String getTitle2() {
        return Title2;
    }
    public String getDescription1() {
        return Description1;
    }
    public String getDescription2() {
        return Description2;
    }
    public int getOnboardingImg() {
        return OnboardingImg;
    }
}
