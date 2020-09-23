package com.rifcode.holalapremium.Models;

public class Gift {
    private String name;
    private String link_json;
    private String image_preview;
    private long Time;


    public Gift() {
    }

    public Gift(String name, String link_json, String image_preview, long time) {
        this.name = name;
        this.link_json = link_json;
        this.image_preview = image_preview;
        Time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink_json() {
        return link_json;
    }

    public void setLink_json(String link_json) {
        this.link_json = link_json;
    }

    public String getImage_preview() {
        return image_preview;
    }

    public void setImage_preview(String image_preview) {
        this.image_preview = image_preview;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
