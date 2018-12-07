package com.uca.devceargo.internic.entities;

public class RoleMapping {
    private int roleMapping;
    private int roleID;
    private String principaltype;
    private String principalID;

    public int getRoleMapping() {
        return roleMapping;
    }

    public void setRoleMapping(int roleMapping) {
        this.roleMapping = roleMapping;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
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
