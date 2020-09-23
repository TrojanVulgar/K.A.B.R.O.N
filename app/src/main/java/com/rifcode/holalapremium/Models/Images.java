package com.rifcode.holalapremium.Models;

public class Images {

    private long Time;
    private String thumb_picture;

    public Images(long time, String thumb_picture) {
        Time = time;
        this.thumb_picture = thumb_picture;
    }

    public Images() {
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getThumb_picture() {
        return thumb_picture;
    }

    public void setThumb_picture(String thumb_picture) {
        this.thumb_picture = thumb_picture;
    }
}
