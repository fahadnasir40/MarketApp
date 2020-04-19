package com.devfn.common.model;


import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;

public class OrderModel implements Serializable {


    private String orderNo,orderId;
    private HashMap<String, CartItem> items;
    private String orderDate;
    private String orderName;
    private String deliveryAddress;
    private int totalOrderPrice;

    public String getSellerMessage() {
        return SellerMessage;
    }

    public void setSellerMessage(String sellerMessage) {
        SellerMessage = sellerMessage;
    }

    private String SellerMessage;
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String deliveryContact;
    private String deliverUserId,orderStatus,deliveryCity,deliveryPostalCode;

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getQuantityOrdered(PostItem postItem){

        if(items.containsKey(postItem.getPostId())){
            return items.get(postItem.getPostId()).getQuantityOrdered();
        }
        return 0;
    }

    public int getPriceOrdered(PostItem postItem){

        if(items.containsKey(postItem.getPostId())){
            return items.get(postItem.getPostId()).getPriceOrdered();
        }
        return 0;
    }


    public void addItems(CartItem post){

        items.put(post.getPostId(),post);
    //    totalOrderPrice += post.getPrice();
    }

    public boolean findPost(PostItem post){
        if(items.containsKey(post.getPostId()))
            return  true;
        return false;
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
        items = new HashMap<String,CartItem>();
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public HashMap<String, CartItem> getItems() {
        return items;
    }

    public void setItems(HashMap<String, CartItem> items) {
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
        this.items = new HashMap<String,CartItem>();
        this.orderDate = orderDate;
        this.orderName = orderName;
        this.deliveryAddress = deliveryAddress;
        this.totalOrderPrice = totalOrderPrice;
        this.deliveryContact = deliveryContact;
    }
}


