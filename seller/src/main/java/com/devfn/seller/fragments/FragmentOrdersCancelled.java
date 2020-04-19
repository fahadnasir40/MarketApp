package com.devfn.seller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.OrderModel;
import com.devfn.seller.R;
import com.devfn.seller.adapters.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentOrdersCancelled extends Fragment
{

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderCancelledList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_cancelled, container, false);

        recyclerView = view.findViewById(R.id.rv_orders_cancelled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderAdapter = new OrderAdapter(orderCancelledList, getContext());
        recyclerView.setAdapter(orderAdapter);

        return view;
    }

    public FragmentOrdersCancelled() {
        orderCancelledList = new ArrayList<>();
    }

    public void addItemsInOrder(OrderModel order){
        orderCancelledList.add(0,order);
        if(orderAdapter != null)
            orderAdapter.notifyDataSetChanged();
    }



    public void clearList(){
        orderCancelledList.clear();
    }
}
