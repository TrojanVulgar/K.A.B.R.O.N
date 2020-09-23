package com.rifcode.holalapremium.Models;



public class ChatObject {

    public String message;
    private String seen;
    private String from;
    private String type;
    private long msgTimeAgo;

    public ChatObject(String message, String seen, String from, String type, long msgTimeAgo) {
        this.message = message;
        this.seen = seen;
        this.from = from;
        this.type = type;
        this.msgTimeAgo = msgTimeAgo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChatObject() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getMsgTimeAgo() {
        return msgTimeAgo;
    }

    public void setMsgTimeAgo(long msgTimeAgo) {
        this.msgTimeAgo = msgTimeAgo;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String userID){
        this.message = message;
    }


}
