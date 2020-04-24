package com.devfn.common.model;

import java.io.Serializable;

public class User implements Serializable {

    private String FullName,email,userId,contactNo,address,postalCode,city,device_token,status,statusTimestamp,visibility;


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

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(String statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public User() {
    }

    public User(String fullName, String email, String userId, String contactNo, String address, String postalCode, String city, String device_token, String status, String statusTimestamp, String visibility) {
        FullName = fullName;
        this.email = email;
        this.userId = userId;
        this.contactNo = contactNo;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.device_token = device_token;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
        this.visibility = visibility;
    }
}
