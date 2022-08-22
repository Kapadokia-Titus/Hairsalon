package com.com.bestlady.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;
import com.com.bestlady.services.repository.ProductRepository;

import java.util.List;

import static com.com.bestlady.ui.HomeScreenActivity.ACTION_SORT_BY_PRICE;
import static com.com.bestlady.ui.HomeScreenActivity.ACTION_SORT_BY_RATING;


public class ProductViewModel extends AndroidViewModel {

    private AppDatabase db;
    private MediatorLiveData<List<Products>> foodDetailsMediatorLiveData = new MediatorLiveData<>();
    private LiveData<List<Products>> foodDetailsLiveDataSortPrice;
    private LiveData<List<Products>> foodDetailsLiveDataSortRating;
    private LiveData<List<CartItem>> cartItemsLiveData;
    private MutableLiveData<Boolean> isFoodCallInProgress = new MutableLiveData<>();
    private static String DEFAULT_SORT = ACTION_SORT_BY_PRICE;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        updatedFishMenu();
        subscribeToFishChanges();
        subscribeToCartChanges();
    }

    private void subscribeToCartChanges() {
        cartItemsLiveData = db.cartItemDao().getCartItems();
    }

    private void updatedFishMenu() {
        isFoodCallInProgress = ProductRepository.getInstance().getFoodMenu(getApplication().getApplicationContext());
    }

    private void subscribeToFishChanges() {
        if(DEFAULT_SORT.equals(ACTION_SORT_BY_PRICE)){
            foodDetailsLiveDataSortPrice = db.foodDetailsDao().getFoodsByPrice();
            foodDetailsMediatorLiveData.addSource(foodDetailsLiveDataSortPrice, new Observer<List<Products>>() {
                @Override
                public void onChanged(@Nullable List<Products> fishDetails) {
                    foodDetailsMediatorLiveData.setValue(fishDetails);
                }
            });
        }else if(DEFAULT_SORT.equals(ACTION_SORT_BY_RATING)){
            foodDetailsLiveDataSortRating = db.foodDetailsDao().getFoodsByRating();
            foodDetailsMediatorLiveData.addSource(foodDetailsLiveDataSortRating, new Observer<List<Products>>() {
                @Override
                public void onChanged(@Nullable List<Products> fishDetails) {
                    foodDetailsMediatorLiveData.setValue(fishDetails);
                }
            });
        }
    }

    public MediatorLiveData<List<Products>> getFoodDetailsMutableLiveData() {
        return foodDetailsMediatorLiveData;
    }

    public void sortFood(String action){
        removeSource(DEFAULT_SORT);
        DEFAULT_SORT = action;
        subscribeToFishChanges();
    }

    private void removeSource(String default_sort) {
        switch (default_sort){
            case ACTION_SORT_BY_PRICE:
                foodDetailsMediatorLiveData.removeSource(foodDetailsLiveDataSortPrice);
                break;
            case ACTION_SORT_BY_RATING:
                foodDetailsMediatorLiveData.removeSource(foodDetailsLiveDataSortRating);
                break;
        }
    }

    public LiveData<Boolean> isFoodUpdateInProgress(){
        return isFoodCallInProgress;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public void updateCart(Products products){
        ProductRepository.getInstance().updateCart(db, products);
    }

}
