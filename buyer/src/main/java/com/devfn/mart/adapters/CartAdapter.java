package com.devfn.mart.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.CartItemInterface;
import com.devfn.mart.R;
import com.devfn.mart.activities.Post;
import com.devfn.common.model.PostItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.PostViewHolder>  {

    private List<PostItem> PostItems;
    private Context        mContext;
    private CartItemInterface cartItem;
    private List<Integer> totalSum;

    public CartAdapter(List<PostItem> PostItems, Context mContext) {
        this.PostItems   = PostItems;
        this.mContext     = mContext;
        this.cartItem = (CartItemInterface)mContext;
        this.totalSum = new ArrayList<>(Collections.nCopies(PostItems.size(), 0));
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cart_item,parent,false);
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
        holder.quantity.setText("Quantity: " );




        int size = PostItems.get(position).getQuantity();
        List<String> quantity = new ArrayList<>(size);
        for(int i = 0;i<size;i++){
            quantity.add(String.valueOf(i+1));
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, quantity);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerItems.setAdapter(dataAdapter);

        //Reset quantity to 1 if items quantity is greater than available items
        if(PostItems.get(position).getQuantityOrdered() > PostItems.get(position).getQuantity()){

            if(PostItems.get(position).getQuantity() > 0){
                PostItems.get(position).setQuantity(1);
                cartItem.RefreshTotal(PostItems.get(position));

            }
            else {
                cartItem.RemoveItemFromCart(PostItems.get(position));
                Toast.makeText(mContext,PostItems.get(position).getName() +" item no longer available",Toast.LENGTH_SHORT).show();
            }
        }
        else{

            holder.spinnerItems.setSelection(PostItems.get(position).getQuantityOrdered() - 1);

        }


        holder.spinnerItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Integer totalQuantity ;
                    NumberFormat myFormat = NumberFormat.getInstance();
                    myFormat.setGroupingUsed(true); // this will also round numbers, 3

                    totalQuantity = Integer.valueOf(adapterView.getItemAtPosition(i).toString());
                    holder.price.setText("Rs. "+ myFormat.format(PostItems.get(position).getPrice() * totalQuantity));

                    PostItems.get(position).setQuantityOrdered(totalQuantity);
                    cartItem.RefreshTotal(PostItems.get(position));

                    totalSum.add(position,PostItems.get(position).getPrice() * totalQuantity);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        holder.deliveryTime.setText("Expected Delivery Time: " + PostItems.get(position).getDeliveryTime() +" days");
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(position);

            }
        });

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
        private Button remove;
        private Spinner spinnerItems;

        ItemClickListener itemClickListener;


        PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            image =  itemView.findViewById(R.id.item_cart_img);
            name =  itemView.findViewById(R.id.item_cart_name);
            price =  itemView.findViewById(R.id.item_cart_price);
            quantity = itemView.findViewById(R.id.item_cart_quantity);
            deliveryTime = itemView.findViewById(R.id.item_cart_delivery_time);
            remove = itemView.findViewById(R.id.btn_cart_remove_item);
            spinnerItems = itemView.findViewById(R.id.spinner_cart_quantity);


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




    private void showDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Remove Item from Cart");


        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
                cartItem.RemoveItemFromCart(PostItems.get(position));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



}
