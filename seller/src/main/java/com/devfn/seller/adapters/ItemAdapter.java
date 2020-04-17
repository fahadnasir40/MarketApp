package com.devfn.seller.adapters;

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

import com.devfn.common.model.PostItem;
import com.devfn.seller.R;
import com.devfn.seller.activities.PostSeller;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.PostViewHolder>{

    private List<PostItem> PostItems;
    private Context        mContext;

    public ItemAdapter(List<PostItem> PostItems, Context mContext) {
        this.PostItems   = PostItems;
        this.mContext     = mContext;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        PostViewHolder PostViewHolder = new PostViewHolder(v);
        return PostViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {


        Picasso.with(mContext).load(PostItems.get(position).getPhoto()).fit()
                .into(holder.image);

        String name = PostItems.get(position).getName();
        if(name.length() > 40){
            name = name.substring(0,40) + "....";
            holder.name.setText(name );
        }
        else
            holder.name.setText(name);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3


        holder.price.setText("Rs. "+ myFormat.format(PostItems.get(position).getPrice()));


        holder.setItemClickListener(new ItemClickListener() {


            public void onItemClickListener(int position) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("post_object_seller",PostItems.get(position));
                Intent intent = new Intent(mContext, PostSeller.class);

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
        private TextView price;
        ItemClickListener itemClickListener;


        PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            image =  itemView.findViewById(R.id.item_img);
            name =  itemView.findViewById(R.id.item_name);
            price =  itemView.findViewById(R.id.item_price);

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