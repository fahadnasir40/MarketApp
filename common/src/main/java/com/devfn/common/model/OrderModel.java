package com.devfn.common.model;


import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {

    private int orderNo;
    private List<PostItem> items;
    private String orderDate;
    private String orderName;
    private String deliveryAddress;
    private int totalOrderPrice;
    private String deliveryContact;

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public List<PostItem> getItems() {
        return items;
    }

    public void setItems(List<PostItem> items) {
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

    public OrderModel(int orderNo, List<PostItem> items, String orderDate, String orderName, String deliveryAddress, int totalOrderPrice, String deliveryContact) {
        this.orderNo = orderNo;
        this.items = items;
        this.orderDate = orderDate;
        this.orderName = orderName;
        this.deliveryAddress = deliveryAddress;
        this.totalOrderPrice = totalOrderPrice;
        this.deliveryContact = deliveryContact;
    }
}


