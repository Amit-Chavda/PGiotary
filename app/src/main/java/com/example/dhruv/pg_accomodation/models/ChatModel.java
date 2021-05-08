package com.example.dhruv.pg_accomodation.models;

public class ChatModel {
    private String chatid;
    private String messageid;
    private String message;
    private String sender;
    private String receiver;

    public ChatModel() {

    }

    public ChatModel(String chatid, String messageid, String message, String sender, String receiver) {
        this.chatid = chatid;
        this.messageid = messageid;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
