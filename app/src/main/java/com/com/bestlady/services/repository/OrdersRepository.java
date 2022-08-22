package com.com.bestlady.services.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Orders;
import com.com.bestlady.model.OrdersResponse;
import com.com.bestlady.services.APIClient;
import com.com.bestlady.services.ProductAPIServices;
import com.com.bestlady.worker.SaveOrdersData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRepository {

    private static OrdersRepository instance;
    private static final String TAG = "FishRepository";
    private String uploadStatus;
    private ProductAPIServices productAPIServices = APIClient.getClient().create(ProductAPIServices.class);
    private OrdersResponse ordersResponse;

    public MutableLiveData<Boolean> getOrdersMenu(final Context context, String sellerId){

        final MutableLiveData<Boolean> isOrdersCallOngoing = new MutableLiveData<>();
        isOrdersCallOngoing.setValue(true);

        productAPIServices.getMyFishOrders(sellerId).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                new SaveOrdersData(AppDatabase.getDatabase(context), response.body()).execute();
                isOrdersCallOngoing.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Log.e(TAG,"response not successful");
            }
        });


        return isOrdersCallOngoing;
    }


    // get my fish posts
    public OrdersResponse submitMyOrders(final Context context, Orders orders){

        ordersResponse = new OrdersResponse();
        productAPIServices.submitFishOrder(orders).enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
               ordersResponse = response.body();
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                uploadStatus = "Uploading error "+ t.getMessage();
            }
        });


        return ordersResponse;
    }

    //get upload status
    public String getUploadStatus(){
        return uploadStatus;
    }
    public static OrdersRepository getInstance() {
        if(instance == null){
            synchronized (OrdersRepository.class){
                if(instance == null){
                    instance = new OrdersRepository();
                }
            }
        }
        return instance;
    }


}
