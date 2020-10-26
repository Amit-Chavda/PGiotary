package com.example.dhruv.pg_accomodation;

public class User {
    String uid;
    String name;
    String emailid;

    public User(){


    }

    public User(String name, String emailid, String uid) {
        this.name = name;
        this.emailid = emailid;
        this.uid = uid;
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
