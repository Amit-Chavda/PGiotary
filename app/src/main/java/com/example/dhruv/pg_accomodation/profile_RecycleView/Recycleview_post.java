package com.example.dhruv.pg_accomodation.profile_RecycleView;

public class Recycleview_post {

    private String postid;
    private String posttitle;
    private String postimage;
    private String postdescription;
    private String postprice;
    private String postaddress;
    private String lat;
    private String longi;
    private String publisher;


    public Recycleview_post(){

    }

    public Recycleview_post(String postid, String posttitle, String postimage, String postdescription, String postprice, String postaddress,String lat ,String longi ,String publisher) {
        this.postid = postid;
        this.posttitle = posttitle;
        this.postimage = postimage;
        this.postdescription = postdescription;
        this.postprice = postprice;
        this.postaddress = postaddress;
        this.lat = lat;
        this.longi = longi;
        this.publisher = publisher;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public String getPostprice() {
        return postprice;
    }

    public void setPostprice(String postprice) {
        this.postprice = postprice;
    }

    public String getPostaddress() {
        return postaddress;
    }

    public void setPostaddress(String postaddress) {
        this.postaddress = postaddress;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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


}
