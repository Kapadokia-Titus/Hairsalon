package com.com.bestlady.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.com.bestlady.R;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;
import com.com.bestlady.ui.adapters.ProductListAdapter;
import com.com.bestlady.utility.ObservableObject;
import com.com.bestlady.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class HomeScreenActivity extends AppCompatActivity implements java.util.Observer, PopupMenu.OnMenuItemClickListener {

    ProductViewModel fishViewModel;
    Observer<List<Products>> foodMenuObserver;
    Observer<List<CartItem>> cartObserver;
    Observer<Boolean> isFoodUpdateInProgressObserver;
    RecyclerView foodList;
    ProductListAdapter productListAdapter;
    AppCompatButton bCart;
    LayoutAnimationController controller;
    ImageView infoImage;
    TextView tInfo,tTotalCost,tCartQuantity;
    Toolbar cartView;
    public static final String INTENT_UPDATE_FOOD = "UPDATE_FOOD";
    public static final String INTENT_UPDATE_LIST = "UPDATE_LIST";
    public static final String ACTION_SORT_BY_PRICE = "SORT_PRICE";
    public static final String ACTION_SORT_BY_RATING = "SORT_RATING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Base);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        fishViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        foodList = findViewById(R.id.food_list);
        tInfo = findViewById(R.id.t_loading);
        infoImage = findViewById(R.id.i_loading);
        cartView = findViewById(R.id.cart_view);
        tTotalCost = cartView.findViewById(R.id.t_total_price);
        tCartQuantity = cartView.findViewById(R.id.t_cart_count);
        bCart = cartView.findViewById(R.id.b_cart);
        bCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreenActivity.this,CartActivity.class));
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(mLayoutManager);
        productListAdapter = new ProductListAdapter(new ArrayList<Products>());
        controller = AnimationUtils.loadLayoutAnimation(foodList.getContext(), R.anim.layout_slide_from_bottom);
        foodList.setAdapter(productListAdapter);
        foodList.scheduleLayoutAnimation();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        foodMenuObserver = new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable List<Products> fishDetails) {
                if(fishDetails !=null){
                    productListAdapter.setData(fishDetails);
                    productListAdapter.notifyDataSetChanged();
                    runLayoutAnimation(foodList);
                }else{
                    Log.e("Fish details","null");
                }
            }
        };
        isFoodUpdateInProgressObserver = new Observer<Boolean>() {
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
        cartObserver = new Observer<List<CartItem>>() {
            @Override
            public void onChanged(@Nullable List<CartItem> cartItems) {
                updateCartUI(cartItems);
            }
        };
        fishViewModel.isFoodUpdateInProgress().observe(this,isFoodUpdateInProgressObserver);
        ObservableObject.getInstance().addObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort){
            showPopup(findViewById(R.id.action_sort));
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.action_sort_price){
            fishViewModel.sortFood(ACTION_SORT_BY_PRICE);
        }else if(menuItem.getItemId() == R.id.action_sort_rating){
            fishViewModel.sortFood(ACTION_SORT_BY_RATING);
        } else if(menuItem.getItemId() == R.id.action_sell_fish){
        startActivity(new Intent(this, SellSamaki.class)); // go to samaki activity
    }
        return false;
    }

    private void updateCartUI(List<CartItem> cartItems) {
        if(cartItems!=null && cartItems.size()>0){
            cartView.setVisibility(View.VISIBLE);
            Double cost = 0.0;
            int quantity = 0;
            for(CartItem cartItem:cartItems){
                cost = cost+(cartItem.getPrice()*cartItem.getQuantity());
                quantity = quantity+cartItem.getQuantity();
            }
            tCartQuantity.setText(String.valueOf(quantity));
            tTotalCost.setText(getString(R.string.rupee_symbol)+ String.valueOf(cost));
        }else{
            cartView.setVisibility(View.GONE);
            tCartQuantity.setText("0");
            tTotalCost.setText(getString(R.string.rupee_symbol)+"0");
        }
    }

    private void subscribeToFoodObserver() {
        if(!fishViewModel.getFoodDetailsMutableLiveData().hasObservers()) {
            fishViewModel.getFoodDetailsMutableLiveData().observe(HomeScreenActivity.this, foodMenuObserver);
        }
        if(!fishViewModel.getCartItemsLiveData().hasObservers()){
            fishViewModel.getCartItemsLiveData().observe(this,cartObserver);
        }
    }

    private void showProgress(boolean show, boolean showList) {
        foodList.setVisibility(showList? View.VISIBLE: View.GONE);
        tInfo.setVisibility(show? View.VISIBLE: View.GONE);
        infoImage.setVisibility(show? View.VISIBLE: View.GONE);
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

    @Override
    protected void onDestroy() {
        fishViewModel.getFoodDetailsMutableLiveData().removeObserver(foodMenuObserver);
        fishViewModel.isFoodUpdateInProgress().removeObserver(isFoodUpdateInProgressObserver);
        fishViewModel.getCartItemsLiveData().removeObserver(cartObserver);
        ObservableObject.getInstance().deleteObserver(this);
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object o) {
        Intent intent = (Intent)o;
        if(intent!=null && intent.getAction() != null) {
            if (intent.getAction().equals(INTENT_UPDATE_FOOD)) {
                fishViewModel.updateCart(productListAdapter.getItem(intent.getIntExtra("position",-1)));
            }else  if(intent.getAction().equals(INTENT_UPDATE_LIST)){
                productListAdapter.notifyDataSetChanged();
            }
        }
    }
}
