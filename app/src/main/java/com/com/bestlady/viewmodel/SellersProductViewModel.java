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
import com.com.bestlady.model.Products;
import com.com.bestlady.services.repository.ProductRepository;

import java.util.List;

public class SellersProductViewModel extends AndroidViewModel {

    private AppDatabase db;
    private LiveData<Products> foodDetailsLiveData;
    private MutableLiveData<Boolean> isFishCallInProgress = new MutableLiveData<>();
    private MediatorLiveData<List<Products>> fishMediatorLiveData = new MediatorLiveData<>();
    private LiveData<List<Products>> foodDetailsLiveDataSortUser;
    public SellersProductViewModel(@NonNull Application application) {
        super(application);
        init();
    }
    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        updatedFishMenu();
    }
    private void updatedFishMenu() {
        isFishCallInProgress = ProductRepository.getInstance().getFoodMenu(getApplication().getApplicationContext());
    }

    public void subscribeToFishChanges(String creatorId) {

            foodDetailsLiveDataSortUser = db.foodDetailsDao().getFishByCreator("123456");
            fishMediatorLiveData.addSource(foodDetailsLiveDataSortUser, new Observer<List<Products>>() {
                @Override
                public void onChanged(@Nullable List<Products> fishDetails) {
                    fishMediatorLiveData.setValue(fishDetails);
                }
        });
    }

    public MediatorLiveData<List<Products>> getMyFishDetailsMutableLiveData() {
        return fishMediatorLiveData;
    }

    public LiveData<Boolean> isFishUpdateInProgress(){
        return isFishCallInProgress;
    }
}
