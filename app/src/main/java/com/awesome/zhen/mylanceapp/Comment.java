package com.awesome.zhen.mylanceapp;

/**
 * Created by zhen on 2/12/2017.
 */

public class Comment {

    private String username;
    private String userimage;
    private String comment;
    private String uid;

    public Comment(){

    }

    public Comment( String username, String userimage, String comment, String uid) {

        this.username = username;
        this.userimage = userimage;
        this.comment = comment;
        this.uid = uid;
    }

    public String getComment() {return comment;}

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getUserImage() {
        return userimage;
    }

    public void setUserImage(String userimage) {this.userimage = userimage;}


}
