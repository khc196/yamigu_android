package com.yamigu.yamigu_app;

public class MainOnboardingItem {
    String Title, Description1, Description2;
    int OnboardingImg;

    public MainOnboardingItem(String title, String description1, String description2, int onboardingImg) {
        Title = title;
        Description1 = description1;
        Description2 = description2;
        OnboardingImg = onboardingImg;
    }
    public void setTitle(String title) {
        Title = title;
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
    public String getTitle() {
        return Title;
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
