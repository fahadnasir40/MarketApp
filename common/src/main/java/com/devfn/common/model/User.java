package com.devfn.common.model;

public class User {

    String FullName,email,userId;

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User(String userId, String fullName, String email) {
        FullName = fullName;
        this.email = email;
        this.userId = userId;
    }
}
