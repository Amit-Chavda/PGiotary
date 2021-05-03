package com.example.dhruv.pg_accomodation.chat_data;

public class UserListModel {
    private String userid;
    private String chatid;

    public UserListModel() {
    }

    public UserListModel(String userid, String chatid) {
        this.userid = userid;
        this.chatid = chatid;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
