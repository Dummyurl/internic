package com.uca.devceargo.internic.entities;

public class UserRoute {
    private int userRouteID;
    private int userID;
    private int routeID;
    private String date;
    private String createAt;

    public int getUserRouteID() {
        return userRouteID;
    }

    public void setUserRouteID(int userRouteID) {
        this.userRouteID = userRouteID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
