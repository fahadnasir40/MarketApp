package com.devfn.common.model;

import java.io.Serializable;

public class User implements Serializable {

    String FullName,email,userId,contactNo,address,postalCode,city;

    public User() {
    }

    public User(String fullName, String email, String userId, String contactNo, String address, String postalCode, String city) {
        FullName = fullName;
        this.email = email;
        this.userId = userId;
        this.contactNo = contactNo;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
    }

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

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
