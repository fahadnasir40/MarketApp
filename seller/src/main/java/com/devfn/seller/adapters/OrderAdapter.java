package com.devfn.seller.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.OrderModel;
import com.devfn.seller.R;
import com.devfn.seller.activities.OrderDetails;

import java.text.NumberFormat;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.PostViewHolder>{

    private List<OrderModel> ordersList;
    private Context        mContext;

    public OrderAdapter(List<OrderModel> ordersList, Context mContext) {
        this.ordersList   = ordersList;
        this.mContext     = mContext;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_item,parent,false);
        PostViewHolder PostViewHolder = new PostViewHolder(v);
        return PostViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {


        holder.orderNumber.setText("Order No. "+ordersList.get(position).getOrderNo());
        holder.orderDate.setText("Placed on "+ordersList.get(position).getDateFromTimeStamp());
        holder.orderStatus.setText(ordersList.get(position).getOrderStatus());

        holder.orderTotalPrice.setText("Rs. "+ getFormattedNumber(ordersList.get(position).getTotalOrderPrice()));

        holder.showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, OrderDetails.class);

                intent.putExtra("order_id",ordersList.get(position).getOrderId());
                intent.putExtra("order_userId",ordersList.get(position).getDeliverUserId());

                mContext.startActivity(intent);
            }
        });

    }

    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView orderNumber,orderDate,orderTotalPrice,orderStatus,showDetails;


        PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            orderNumber =  itemView.findViewById(R.id.order_tv_order_no);
            orderDate =  itemView.findViewById(R.id.order_tv_order_date);
            orderTotalPrice =  itemView.findViewById(R.id.order_item_price);
            orderStatus =  itemView.findViewById(R.id.item_order_status);
            showDetails = itemView.findViewById(R.id.item_order_show_details);

        }
    }


}
