package com.example.dhruv.pg_accomodation;

public class User {
    String uid;
    String name;
    String emailid;
    String profileimg;

    public User(){


    }

    public User(String profileimg) {
        this.profileimg = profileimg;
    }

    public User(String name, String emailid, String uid) {
        this.name = name;
        this.emailid = emailid;
        this.uid = uid;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmailid() {
        return emailid;
    }
}
