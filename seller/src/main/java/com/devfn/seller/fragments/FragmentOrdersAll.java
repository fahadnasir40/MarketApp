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

public class FragmentOrdersAll extends Fragment
{

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderAllList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_all, container, false);

        recyclerView = view.findViewById(R.id.rv_orders_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderAdapter = new OrderAdapter(orderAllList, getContext());
        recyclerView.setAdapter(orderAdapter);

        return view;
    }

    public FragmentOrdersAll() {
        orderAllList = new ArrayList<>();
    }

    public void addItemsInOrder(OrderModel order){
        orderAllList.add(0,order);
        orderAdapter.notifyDataSetChanged();
    }



    public void clearList(){
        orderAllList.clear();
    }
}
