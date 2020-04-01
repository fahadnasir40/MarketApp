package com.devfn.common.model;


import java.io.Serializable;
import java.util.HashMap;

public class OrderModel implements Serializable {

    private String orderNo;
    private HashMap<String, PostItem> items;
    private String orderDate;
    private String orderName;
    private String deliveryAddress;
    private int totalOrderPrice;
    private String deliveryContact;
    private String deliverUserId;

    private static OrderModel instance = null;



    public void addItems(PostItem post){

        items.put(post.getPostId(),post);
        totalOrderPrice += post.getPrice();
    }



    public String getDeliverUserId() {
        return deliverUserId;
    }

    public void setDeliverUserId(String deliverUserId) {
        this.deliverUserId = deliverUserId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public OrderModel() {
        items = new HashMap<String,PostItem>();
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public HashMap<String, PostItem> getItems() {
        return items;
    }

    public void setItems(HashMap<String, PostItem> items) {
        this.items = items;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public int getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(int totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public String getDeliveryContact() {
        return deliveryContact;
    }

    public void setDeliveryContact(String deliveryContact) {
        this.deliveryContact = deliveryContact;
    }

    public OrderModel(String orderNo, String orderDate, String orderName, String deliveryAddress, int totalOrderPrice, String deliveryContact) {
        this.orderNo = orderNo;
        this.items = new HashMap<String,PostItem>();
        this.orderDate = orderDate;
        this.orderName = orderName;
        this.deliveryAddress = deliveryAddress;
        this.totalOrderPrice = totalOrderPrice;
        this.deliveryContact = deliveryContact;
    }
}


