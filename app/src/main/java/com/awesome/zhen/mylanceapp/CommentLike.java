package com.awesome.zhen.mylanceapp;

/**
 * Created by zhen on 2/12/2017.
 */

public class CommentLike {

    private String username;
    private String userimage;
    private String message;
    private String uid;
    private String blogpost;
    private String location;
    private String category;
    private String commentkey;
    private String comment;

    public CommentLike(){

    }

    public CommentLike(String username, String userimage, String message, String uid, String blogpost, String location, String category, String commentkey, String comment) {

        this.username = username;
        this.userimage = userimage;
        this.message = message;
        this.uid = uid;
        this.blogpost = blogpost;
        this.commentkey = commentkey;
        this.location = location;
        this.category = category;
        this.comment = comment;
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getUserImage() {
        return userimage;
    }

    public void setUserImage(String userimage) {this.userimage = userimage;}

    public String getBlogpost() {return blogpost;}

    public void setBlogpost(String blogpost) {
        this.blogpost = blogpost;
    }

    public String getLocation() {return location;}

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {return category;}

    public void setCategory (String category) {
        this.category = category;
    }

    public String getCommentkey() {return commentkey;}

    public void setCommentkey (String commentkey) {
        this.commentkey = commentkey;
    }


}
