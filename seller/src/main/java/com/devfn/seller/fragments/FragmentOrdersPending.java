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

public class FragmentOrdersPending extends Fragment
{

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderPendingList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_pending, container, false);

        recyclerView = view.findViewById(R.id.rv_orders_pending);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderAdapter = new OrderAdapter(orderPendingList, getContext());
        recyclerView.setAdapter(orderAdapter);

        return view;
    }

    public FragmentOrdersPending() {
        orderPendingList = new ArrayList<>();
    }

    public void addItemsInOrder(OrderModel order,boolean pending){

        if(pending)
            orderPendingList.add(0,order);
        else
            orderPendingList.add(order);

        if(orderAdapter != null)
            orderAdapter.notifyDataSetChanged();
    }



    public void clearList(){
        orderPendingList.clear();
    }
}
