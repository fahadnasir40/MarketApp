package com.devfn.common.model;

public interface CartItemInterface {

     void RemoveItemFromCart(PostItem post);
     void RefreshTotal(PostItem postItem);
     void RefreshItemPrice(PostItem postItem,int oldPrice);
}
