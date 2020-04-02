package com.devfn.common.model;

public class CartItem {

    private String postId;
    private Integer quantityOrdered;

    public CartItem() {
    }

    public CartItem(String postId, Integer quantityOrdered) {
        this.postId = postId;
        this.quantityOrdered = quantityOrdered;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
}
