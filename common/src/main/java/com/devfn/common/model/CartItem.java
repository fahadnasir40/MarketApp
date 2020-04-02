package com.devfn.common.model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String postId;
    private int quantityOrdered;

    public CartItem() {
        quantityOrdered = 0;
    }

    public CartItem(String postId, int quantityOrdered) {
        this.postId = postId;
        this.quantityOrdered = quantityOrdered;
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
