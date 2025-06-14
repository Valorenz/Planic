package com.example.planic.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String username;
    private String email;
    private String password;
    private Timestamp createdTimestamp;
    private String userId;

    public UserModel() {
    }

    public UserModel(String username, String email, String password, Timestamp createdTimestamp, String userId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}