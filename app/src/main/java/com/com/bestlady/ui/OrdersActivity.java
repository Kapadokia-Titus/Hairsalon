package com.com.bestlady.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.com.bestlady.R;
import com.com.bestlady.model.Orders;
import com.com.bestlady.ui.adapters.OrdersAdapter;
import com.com.bestlady.viewmodel.OrdersViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView ordersRecycler;
    OrdersViewModel ordersViewModel;
    OrdersAdapter ordersAdapter;
    LayoutAnimationController controller;
    Observer<List<Orders>> myOrderObserver;
    Observer<Boolean> isOrderUpdateInProgressObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRecycler = findViewById(R.id.orders_recycler);
        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel.class);
        //recycler init
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        ordersRecycler.setLayoutManager(mLayoutManager);
        ordersAdapter = new OrdersAdapter(new ArrayList<Orders>(),ordersViewModel);
        controller = AnimationUtils.loadLayoutAnimation(ordersRecycler.getContext(), R.anim.layout_slide_from_bottom);
        ordersRecycler.setAdapter(ordersAdapter);
        ordersRecycler.scheduleLayoutAnimation();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        myOrderObserver = new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> ordersList) {
                ordersAdapter.setData(ordersList);
                ordersAdapter.notifyDataSetChanged();
                runLayoutAnimation(ordersRecycler);
            }
        };

        isOrderUpdateInProgressObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean!=null && !aBoolean){
                    showProgress(false,true);
                    subscribeToOrdersObserver();
                }else{
                    showProgress(true,false);
                }
            }
        };
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        if(recyclerView.getAdapter()!=null) {
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
        }
    }

    private void subscribeToOrdersObserver() {
        if(!ordersViewModel.getOrdersMutableLiveData().hasObservers()) {
            ordersViewModel.getOrdersMutableLiveData().observe(OrdersActivity.this, myOrderObserver);
        }

    }

    private void showProgress(boolean show, boolean showList) {
        ordersRecycler.setVisibility(showList? View.VISIBLE: View.GONE);
    }

    @Override
    protected void onDestroy() {
        ordersViewModel.getOrdersMutableLiveData().removeObserver(myOrderObserver);
        super.onDestroy();
    }
}