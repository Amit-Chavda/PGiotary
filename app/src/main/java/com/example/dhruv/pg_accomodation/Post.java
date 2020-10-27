package com.example.dhruv.pg_accomodation;

public class Post {
    private String postid;
    private String posttitle;
    private String postimage;
    private String postdescription;
    private String postprice;
    private String postaddress;
    private String publisher;

    public Post(){

    }

    public Post(String postid, String posttitle, String postimage, String postdescription, String postprice, String postaddress, String publisher) {
        this.postid = postid;
        this.posttitle = posttitle;
        this.postimage = postimage;
        this.postdescription = postdescription;
        this.postprice = postprice;
        this.postaddress = postaddress;
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
}//end of class
