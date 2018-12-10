package com.uca.devceargo.internic.entities;

import java.util.List;

public class Cooperative {
    private int id;
    private int locationID;
    private int contactType;
    private String name;
    private String description;
    private String urlCoverImage;
    private String urlShield;
    private String qualification;
    private String fullName;
    private String createAt;
    private String contactNumber;
    private Location location;
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public int getLocationID() {
        return locationID;
    }

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

    public String getUrlCoverImage() {
        return urlCoverImage;
    }

    public void setUrlCoverImage(String urlCoverImage) {
        this.urlCoverImage = urlCoverImage;
    }

    public String getUrlShield() {
        return urlShield;
    }

    public void setUrlShield(String urlShield) {
        this.urlShield = urlShield;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
