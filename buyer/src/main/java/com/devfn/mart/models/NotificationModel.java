package com.devfn.mart.models;

import com.devfn.common.model.OrderModel;
import com.devfn.common.model.PostItem;

public class NotificationModel {

    private String notificationId;
    private String userId;
    private String type;
    private String date;
    private String title,orderId,postId;
    private int image;


    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public NotificationModel() {
    }

    public NotificationModel(String notificationId, String userId, String type, String date, String title, int image) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.title = title;
        this.image = image;
    }
}
