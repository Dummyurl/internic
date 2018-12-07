package com.uca.devceargo.internic.entities;

public class Interlocal {
    private int interlocalID;
    private int cooperativeID;
    private String plateNumber;
    private String fullNameDriver;
    private String urlImage;
    private String createAt;

    public int getInterlocalID() {
        return interlocalID;
    }

    public void setInterlocalID(int interlocalID) {
        this.interlocalID = interlocalID;
    }

    public int getCooperativeID() {
        return cooperativeID;
    }

    public void setCooperativeID(int cooperativeID) {
        this.cooperativeID = cooperativeID;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getFullNameDriver() {
        return fullNameDriver;
    }

    public void setFullNameDriver(String fullNameDriver) {
        this.fullNameDriver = fullNameDriver;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
