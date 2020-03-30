package com.devfn.mart.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.mart.R;
import com.devfn.mart.activities.Post;
import com.devfn.mart.model.PostItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.PostViewHolder>{

    private List<PostItem> PostItems;
    private Context        mContext;

    public CartAdapter(List<PostItem> PostItems, Context mContext) {
        this.PostItems   = PostItems;
        this.mContext     = mContext;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cart_item,parent,false);
        PostViewHolder PostViewHolder = new PostViewHolder(v);
        return PostViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3


        Picasso.with(mContext).load(PostItems.get(position).getPhoto()).fit().centerCrop()
                .placeholder(R.drawable.ic_add_shopping_cart_black_24dp)
                .error(R.drawable.ic_close_black_24dp)
                .into(holder.image);

        holder.name.setText(PostItems.get(position).getName());
        holder.price.setText("Rs. "+ myFormat.format(PostItems.get(position).getPrice()));
        holder.quantity.setText("Quantity: " + Integer.toString(PostItems.get(position).getQuantity()));
        holder.deliveryTime.setText("Expected Delivery Time: " + PostItems.get(position).getDeliveryTime());

        holder.setItemClickListener(new ItemClickListener() {


            public void onItemClickListener(int position) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("post_object",PostItems.get(position));
                Intent intent = new Intent(mContext, Post.class);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return PostItems.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView image;
        private TextView name;
        private TextView price,quantity,deliveryTime;

        ItemClickListener itemClickListener;


        PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            image =  itemView.findViewById(R.id.item_cart_img);
            name =  itemView.findViewById(R.id.item_cart_name);
            price =  itemView.findViewById(R.id.item_cart_price);
            quantity = itemView.findViewById(R.id.item_cart_quantity);
            deliveryTime = itemView.findViewById(R.id.item_cart_delivery_time);

            itemView.setOnClickListener(this);
        }


        public void onClick(View view) {
            this.itemClickListener.onItemClickListener(getLayoutPosition());
        }

        void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener = ic;
        }

    }

    public interface ItemClickListener
    {
        void onItemClickListener(int position);
    }

}
