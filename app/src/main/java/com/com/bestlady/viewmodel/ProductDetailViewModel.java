package com.com.bestlady.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;
import com.com.bestlady.services.repository.ProductRepository;

import java.util.List;


public class ProductDetailViewModel extends AndroidViewModel {

    private AppDatabase db;
    private LiveData<List<CartItem>> cartItemsLiveData;
    private LiveData<Products> foodDetailsLiveData;

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        subscribeToCartChanges();
    }

    private void subscribeToCartChanges() {
        cartItemsLiveData = db.cartItemDao().getCartItems();
    }

    public void subscribeForFoodDetails(String name){
        foodDetailsLiveData = db.foodDetailsDao().getFood(name);
    }

    public LiveData<Products> getFoodDetailsLiveData(){
        return foodDetailsLiveData;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public void updateCart(Products products){
        ProductRepository.getInstance().updateCart(db, products);
        db.foodDetailsDao().save(products);
    }
}
