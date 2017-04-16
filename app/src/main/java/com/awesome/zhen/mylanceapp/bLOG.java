package com.awesome.zhen.mylanceapp;

/**
 * Created by zhen on 2/12/2017.
 */

public class bLOG {

    private String title;
    private String desc;
    private String image;
    private String username;
    private String userimage;
    private String uid;
    private String telephone;
    private String location;
    private String key;
    private String category;

    public bLOG(){
        
    }

    public bLOG(String title, String desc, String image, String username, String userimage, String uid, String telephone, String location, String key, String category) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.userimage = userimage;
        this.uid = uid;
        this.telephone = telephone;
        this.location = location;
        this.key = key;
        this.category = category;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {this.image = image;}

    public String getTitle() {return title;}
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {return desc;}
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {return key;}
    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {return category;}
    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {return uid;}
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getTelephone() {return telephone;}
    public void setTelephone(String telephone) {this.telephone = telephone;}

    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}

    public String getuserImage() {
        return userimage;
    }
    public void setuserImage(String userimage) {this.userimage = userimage;}

}
