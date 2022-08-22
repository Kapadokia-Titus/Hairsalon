package com.com.bestlady.services.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Products;
import com.com.bestlady.model.ProductResponse;
import com.com.bestlady.services.APIClient;
import com.com.bestlady.services.ProductAPIServices;
import com.com.bestlady.worker.SaveProduct;
import com.com.bestlady.worker.UpdateCart;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private static ProductRepository instance;
    private static final String TAG = "FishRepository";
    private String uploadStatus;
    private ProductAPIServices productAPIServices = APIClient.getClient().create(ProductAPIServices.class);

    public MutableLiveData<Boolean> getFoodMenu(final Context context){

        final MutableLiveData<Boolean> isFoodCallOngoing = new MutableLiveData<>();
        isFoodCallOngoing.setValue(true);

        productAPIServices.getFoodData().enqueue(new Callback<List<Products>>() {
            @Override
            public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                if(response.isSuccessful()) {
                    new SaveProduct(AppDatabase.getDatabase(context), response.body()).execute();
                    isFoodCallOngoing.setValue(false);
                }else{
                    Log.e(TAG,"response not successful");
                }
            }

            @Override
            public void onFailure(Call<List<Products>> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
        return isFoodCallOngoing;
    }

    // get my fish posts
    public MutableLiveData<Boolean> uploadMyFishPosts(final Context context, Products products){

        final MutableLiveData<Boolean> isFishCallOngoing = new MutableLiveData<>();
        isFishCallOngoing.setValue(true);
        productAPIServices.submitFishData(products).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                uploadStatus=response.message();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                uploadStatus = "Uploading error "+ t.getMessage();
            }
        });

        return isFishCallOngoing;
    }

    //get upload status
    public String getUploadStatus(){
        return uploadStatus;
    }
    public static ProductRepository getInstance() {
        if(instance == null){
            synchronized (ProductRepository.class){
                if(instance == null){
                    instance = new ProductRepository();
                }
            }
        }
        return instance;
    }

    public void updateCart(final AppDatabase db, Products products) {
        new UpdateCart(db).execute(products);
    }
}
