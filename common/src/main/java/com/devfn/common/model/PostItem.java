package com.devfn.common.model;

import java.io.Serializable;

public class PostItem implements Serializable {

    private String postId;
    private String photo;
    private int quantity,price;
    private String name,description,deliveryTime,datePosted,authorId;

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public PostItem(String postId, String authorId, String photo, int quantity, int price, String datePosted, String name, String description, String deliveryTime) {

        this.authorId = authorId;
        this.postId = postId;
        this.photo = photo;
        this.quantity = quantity;
        this.price = price;
        this.datePosted = datePosted;
        this.name = name;
        this.description = description;
        this.deliveryTime = deliveryTime;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public PostItem() {
    }
}


