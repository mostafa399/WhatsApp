package com.example.whatsapp.model;

public class UserModel {
    private String userName;
    private String ImageUrl;
    private String id;
    private String Status;

    public UserModel(String userName, String imageUrl, String id, String status) {
        this.userName = userName;
        ImageUrl = imageUrl;
        this.id = id;
        this.Status = status;
    }

    public UserModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
}