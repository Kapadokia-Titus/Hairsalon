package com.com.bestlady.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Products;
import com.com.bestlady.services.repository.ProductRepository;

import java.util.List;

public class SellProductViewModel extends AndroidViewModel {

    private AppDatabase db;
    private MediatorLiveData<List<Products>> fishDetailsMediatorLiveData = new MediatorLiveData<>();
    public SellProductViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
    }

    public String uploadFishData(Products products) {
        if (products ==null||  products.getCreatorId() ==null || products.getImageUrl() ==null
        || products.getName().isEmpty() || products.getPrice() == null || products.getQuantity()==null){

            return "Error Submitting your Order";
        }
        else{
            ProductRepository.getInstance().uploadMyFishPosts(getApplication().getApplicationContext(), products);
            return  ProductRepository.getInstance().getUploadStatus();
        }
    }
}
