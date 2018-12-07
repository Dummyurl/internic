package com.uca.devceargo.internic.entities;

public class Acl {
    private int aclID;
    private String model;
    private String property;
    private String accessType;
    private String permission;
    private String principaltype;
    private String principalID;

    public int getAclID() {
        return aclID;
    }

    public void setAclID(int aclID) {
        this.aclID = aclID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPrincipaltype() {
        return principaltype;
    }

    public void setPrincipaltype(String principaltype) {
        this.principaltype = principaltype;
    }

    public String getPrincipalID() {
        return principalID;
    }

    public void setPrincipalID(String principalID) {
        this.principalID = principalID;
    }
}
