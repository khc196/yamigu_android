package com.yamigu.yamigu_app.Etc.Model;

public class NotificationData
{
    public String id;
    public boolean isUnread;
    public long type;
    public long time;
    public String content;
    public NotificationData() {

    }
    public String getId(){
        return id;
    }
    public long getTime() {
        return time;
    }

    public long getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
