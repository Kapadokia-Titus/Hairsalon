package com.com.bestlady.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.com.bestlady.R;
import com.com.bestlady.model.Products;
import com.com.bestlady.ui.adapters.SellersProductAdapter;
import com.com.bestlady.utility.PreferenceKeys;
import com.com.bestlady.viewmodel.SellersProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class SellersProductsActivity extends AppCompatActivity {
    private static final String TAG = "SellersProductsActivity";
    private SellersProductAdapter sellersProductAdapter;
    SellersProductViewModel sellersProductViewModel;
    Observer<List<Products>> myFishObserver;
    Observer<Boolean> isFishUpdateInProgressObserver;
    ImageView infoImage;
    TextView tInfo;
    RecyclerView fishList;
    LayoutAnimationController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_products);

        //initialize view model
        sellersProductViewModel = ViewModelProviders.of(this).get(SellersProductViewModel.class);
        fishList = findViewById(R.id.my_fish_recycler);
        tInfo = findViewById(R.id.t_loading);
        infoImage = findViewById(R.id.i_loading);

        //recycler init
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        fishList.setLayoutManager(mLayoutManager);
        sellersProductAdapter = new SellersProductAdapter(new ArrayList<Products>());
        controller = AnimationUtils.loadLayoutAnimation(fishList.getContext(), R.anim.layout_slide_from_bottom);
        fishList.setAdapter(sellersProductAdapter);
        fishList.scheduleLayoutAnimation();

        //fetch data by creatorId
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SellersProductsActivity.this);
        String status= preferences.getString(PreferenceKeys.logged_in_value, "");
        Log.d(TAG, "onCreate: "+status);
        if (status != null){ sellersProductViewModel.subscribeToFishChanges(status);}
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myFishObserver = new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable List<Products> fishDetails) {
                if(fishDetails !=null){
                    sellersProductAdapter.setData(fishDetails);
                    sellersProductAdapter.notifyDataSetChanged();
                    runLayoutAnimation(fishList);
                }else{
                    Log.e("Fish details","null");
                }
            }
        };
        isFishUpdateInProgressObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean!=null && !aBoolean){
                    showProgress(false,true);
                    subscribeToFoodObserver();
                }else{
                    showProgress(true,false);
                }
            }
        };

        sellersProductViewModel.isFishUpdateInProgress().observe(this,isFishUpdateInProgressObserver);
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
    private void subscribeToFoodObserver() {
        if(!sellersProductViewModel.getMyFishDetailsMutableLiveData().hasObservers()) {
            sellersProductViewModel.getMyFishDetailsMutableLiveData().observe(SellersProductsActivity.this, myFishObserver);
        }

    }

    private void showProgress(boolean show, boolean showList) {
        fishList.setVisibility(showList? View.VISIBLE: View.GONE);
        tInfo.setVisibility(show? View.VISIBLE: View.GONE);
        infoImage.setVisibility(show? View.VISIBLE: View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sellers_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.my_orders_item){
            startActivity(new Intent(this, OrdersActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }


}