package com.devfn.mart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.PostItem;
import com.devfn.mart.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;


public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.PostViewHolder>  {

    private List<PostItem> PostItems;
    private Context        mContext;
    private List<CartItem> cartItemList;

    public OrderDetailsAdapter(List<PostItem> PostItems, List<CartItem> cartItemList, Context mContext) {
        this.PostItems   = PostItems;
        this.mContext     = mContext;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_details_item,parent,false);
        PostViewHolder PostViewHolder = new PostViewHolder(v);
        return PostViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {

        Picasso.with(mContext).load(PostItems.get(position).getPhoto()).fit().centerCrop()
                .placeholder(R.drawable.ic_add_shopping_cart_black_24dp)
                .error(R.drawable.ic_close_black_24dp)
                .into(holder.image);

        holder.name.setText(PostItems.get(position).getName());

        for(CartItem cartItem:cartItemList){
            if(cartItem.getPostId().equals(PostItems.get(position).getPostId()))        {
                holder.quantity.setText("Quantity Ordered: " + cartItem.getQuantityOrdered() );
                break;
            }
        }

        holder.deliveryTime.setText("Expected Delivery Time: " + PostItems.get(position).getDeliveryTime() +" days");
        holder.price.setText("Rs. " + getFormattedNumber(PostItems.get(position).getPrice()));
    }

    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }


    @Override
    public int getItemCount() {
        return PostItems.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView price,quantity,deliveryTime;


        PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            image =  itemView.findViewById(R.id.item_details_img);
            name =  itemView.findViewById(R.id.item_details_name);
            price =  itemView.findViewById(R.id.item_details_price);
            quantity = itemView.findViewById(R.id.item_details_quantity);
            deliveryTime = itemView.findViewById(R.id.item_details_delivery_time);

        }
    }


}
