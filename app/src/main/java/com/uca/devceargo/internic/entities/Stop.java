package com.uca.devceargo.internic.entities;

import java.util.List;

public class Stop {

    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String createAt;
    private int id;
    private int routeID;
    private List<PictureStop> pictureStop;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public List<PictureStop> getPictureStop() {
        return pictureStop;
    }

    public void setPictureStop(List<PictureStop> pictureStop) {
        this.pictureStop = pictureStop;
    }
}
