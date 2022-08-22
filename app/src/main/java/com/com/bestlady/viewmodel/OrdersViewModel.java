package com.com.bestlady.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Orders;
import com.com.bestlady.services.repository.OrdersRepository;

import java.util.List;

public class OrdersViewModel extends AndroidViewModel {

    private AppDatabase db;
    private MediatorLiveData<List<Orders>> ordersMediatorLiveData = new MediatorLiveData<>();
    private MutableLiveData<Boolean> isOrdersCallInProgress = new MutableLiveData<>();

    public OrdersViewModel(@NonNull Application application) {
        super(application);
        init("123456");
    }

    private void init(String sellerId) {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        updatedOrders(sellerId);
        ordersMenu(sellerId);
    }
    private void updatedOrders(String sellerId) {
        isOrdersCallInProgress = OrdersRepository.getInstance().getOrdersMenu(getApplication().getApplicationContext(), sellerId);
    }
    private void ordersMenu(String sellerId) {
        LiveData<List<Orders>> ordersLiveData = db.orderDao().getOrdersByCreator(sellerId);
        ordersMediatorLiveData.addSource(ordersLiveData, new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> ordersList) {
                ordersMediatorLiveData.setValue(ordersList);
            }
        });

    }

    public MediatorLiveData<List<Orders>> getOrdersMutableLiveData() {
        return ordersMediatorLiveData;
    }
    public LiveData<Boolean> isOrderInProgress(){
        return isOrdersCallInProgress;
    }
}
