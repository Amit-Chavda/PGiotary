package com.example.dhruv.pg_accomodation;

public class Post {
    private String postId;
    private String postType;
    private String postImage;
    private String postDescription;
    private String postRent;
    private String postAddress;
    private String postRatings;
    private String postStatus;
    private String postOwner;
    private String lat;
    private String longi;
    private String facility;

    public Post(){}

    public Post(String postId, String postType, String postImage, String postDescription, String postRent, String postAddress, String postRatings, String postStatus, String postOwner, String lat, String longi, String facility) {
        this.postId = postId;
        this.postType = postType;
        this.postImage = postImage;
        this.postDescription = postDescription;
        this.postRent = postRent;
        this.postAddress = postAddress;
        this.postRatings = postRatings;
        this.postStatus = postStatus;
        this.postOwner = postOwner;
        this.lat = lat;
        this.longi = longi;
        this.facility = facility;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostRent() {
        return postRent;
    }

    public void setPostRent(String postRent) {
        this.postRent = postRent;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostRatings() {
        return postRatings;
    }

    public void setPostRatings(String postRatings) {
        this.postRatings = postRatings;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }
}
