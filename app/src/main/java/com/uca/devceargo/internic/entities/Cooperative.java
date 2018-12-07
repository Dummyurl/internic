package com.uca.devceargo.internic.entities;

public class Cooperative {
    private int id;
    private String name;
    private String description;
    private String urlCoverImage;
    private String urlShield;
    private String qualification;
    private String fullName;
    private String createAt;
    private Location location;

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
