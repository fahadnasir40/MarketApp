package com.devfn.common.model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String postId;
    private int quantityOrdered;

    private int priceOrdered;

    public CartItem() {
        quantityOrdered = 0;
    }

    public int getPriceOrdered() {
        return priceOrdered;
    }

    public void setPriceOrdered(int priceOrdered) {
        this.priceOrdered = priceOrdered;
    }

    public CartItem(String postId, int quantityOrdered, int priceOrdered) {
        this.postId = postId;
        this.quantityOrdered = quantityOrdered;
        this.priceOrdered = priceOrdered;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getQuantityOrderedByPost(String postId){
        if(this.postId.equals(postId))
            return quantityOrdered;
        return 0;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
}
