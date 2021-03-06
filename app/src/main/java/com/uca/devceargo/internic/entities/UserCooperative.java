package com.uca.devceargo.internic.entities;

public class UserCooperative {
    private int id;
    private int userID;
    private int cooperativeID;
    private String createAt;
    private Cooperative cooperative;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cooperative getCooperative() {
        return cooperative;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCooperativeID() {
        return cooperativeID;
    }

    public void setCooperativeID(int cooperativeID) {
        this.cooperativeID = cooperativeID;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
