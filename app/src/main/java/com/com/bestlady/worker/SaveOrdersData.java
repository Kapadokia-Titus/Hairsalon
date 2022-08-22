package com.com.bestlady.worker;

import android.os.AsyncTask;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class SaveOrdersData extends AsyncTask<Void, Void, Void> {
    private AppDatabase db;
    private List<Orders> ordersList;
    public SaveOrdersData(AppDatabase db, List<Orders> ordersList) {
        this.db = db;
        this.ordersList = ordersList;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(db!=null){
            if(ordersList !=null && ordersList.size()>0) {
                List<String> nameList = new ArrayList<>();

                db.orderDao().save(ordersList);
                db.orderDao().deleteOtherOrders(nameList);
            }else{
                db.orderDao().deleteAll();
            }
        }
        return null;
    }
}
