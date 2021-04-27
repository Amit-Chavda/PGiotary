package com.example.dhruv.pg_accomodation;

public class UserModel {

    private String username;
    private String email;
    private String mobileNumber;
    private String profileImage;
    private String city;
    private String userId;
    private String password;

    public UserModel(){};

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserModel(String username, String email,String password, String mobileNumber, String profileImage, String city, String userId) {
        this.username = username;
        this.email = email;
        this.password=password;
        this.mobileNumber = mobileNumber;
        this.profileImage = profileImage;
        this.city = city;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
