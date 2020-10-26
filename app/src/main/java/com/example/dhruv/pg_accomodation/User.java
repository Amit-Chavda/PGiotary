package com.example.dhruv.pg_accomodation;

public class User {

    String name;
    String emailid;

    public User(){


    }

    public User(String name, String emailid) {
        this.name = name;
        this.emailid = emailid;
    }

    public String getName() {
        return name;
    }

    public String getEmailid() {
        return emailid;
    }
}
