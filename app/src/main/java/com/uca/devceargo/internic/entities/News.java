package com.uca.devceargo.internic.entities;

import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("id")
    private int newsID;
    private int locationID;
    private int cooperativeID;
    private int typeNewID;
    private String createAt;
    private String title;
    private String description;

    //private User user;
   // private TypeNews typeNews;
    private Cooperative cooperative;

   /* public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeNews getTypeNews() {
        return typeNews;
    }

    public void setTypeNews(TypeNews typeNews) {
        this.typeNews = typeNews;
    }*/

    public Cooperative getCooperative() {
        return cooperative;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNewsID() {
        return newsID;
    }

    public void setNewsID(int newsID) {
        this.newsID = newsID;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public int getCooperativeID() {
        return cooperativeID;
    }

    public void setCooperativeID(int cooperativeID) {
        this.cooperativeID = cooperativeID;
    }

    public int getTypeNewID() {
        return typeNewID;
    }

    public void setTypeNewID(int typeNewID) {
        this.typeNewID = typeNewID;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
