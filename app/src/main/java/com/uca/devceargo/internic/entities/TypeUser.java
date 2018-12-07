package com.uca.devceargo.internic.entities;

public class TypeUser {
    private int typeUserID;
    private String name;
    private String description;
    private String createAt;

    public int getTypeUserID() {
        return typeUserID;
    }

    public void setTypeUserID(int typeUserID) {
        this.typeUserID = typeUserID;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
