package com.uca.devceargo.internic.entities;

public class Comment {
    private int id;
    private String urlImage;
    private String title;
    private String description;
    private String createAt;
    private int userID;
    private int cooperativeID;
    private int typeCommentID;
    //Entities
    private Cooperative cooperative;
    private User user;
    private TypeComment typeComment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
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

    public int getTypeCommentID() {
        return typeCommentID;
    }

    public void setTypeCommentID(int typeCommentID) {
        this.typeCommentID = typeCommentID;
    }

    public Cooperative getCooperative() {
        return cooperative;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeComment getTypeComment() {
        return typeComment;
    }

    public void setTypeComment(TypeComment typeComment) {
        this.typeComment = typeComment;
    }
}
