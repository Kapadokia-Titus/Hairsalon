package com.com.bestlady.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.com.bestlady.R;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;
import com.com.bestlady.viewmodel.ProductDetailViewModel;

import java.util.List;


public class IndividualActivity extends AppCompatActivity implements View.OnClickListener {

    private ProductDetailViewModel fishDetailViewModel;
    Observer<Products> foodDetailObserver;
    private ImageView iFoodImage;
    private TextView tName,tCost,tQuantity,tTotalCost,tCartQuantity;
    private Toolbar cartView;
    Observer<List<CartItem>> cartObserver;
    private Products duplicateProducts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String foodId = "";
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey("name")) {
                foodId = bundle.getString("name");
            }
        }
        if(foodId !=null && !foodId.isEmpty()) {
            iFoodImage = findViewById(R.id.i_food_image);
            tName = findViewById(R.id.t_name);
            tCost = findViewById(R.id.t_cost);
            tQuantity = findViewById(R.id.t_quantity);
            ImageView iPlus = findViewById(R.id.i_plus);
            ImageView iMinus = findViewById(R.id.i_minus);
            iPlus.setOnClickListener(this);
            iMinus.setOnClickListener(this);
            cartView = findViewById(R.id.cart_view);
            tTotalCost = findViewById(R.id.t_total_price);
            tCartQuantity = findViewById(R.id.t_cart_count);
            Button bCart = findViewById(R.id.b_cart);
            bCart.setOnClickListener(this);

            fishDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel.class);
            fishDetailViewModel.subscribeForFoodDetails(foodId);
            foodDetailObserver = new Observer<Products>() {
                @Override
                public void onChanged(@Nullable Products products) {
                    updateUI(products);
                }
            };
            cartObserver = new Observer<List<CartItem>>() {
                @Override
                public void onChanged(@Nullable List<CartItem> cartItems) {
                    updateCartUI(cartItems);
                }
            };
            fishDetailViewModel.getFoodDetailsLiveData().observe(this, foodDetailObserver);
            fishDetailViewModel.getCartItemsLiveData().observe(this, cartObserver);
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            onBackPressed();
        }
        return true;
    }*/

    private void updateUI(Products products) {
        duplicateProducts = products;
        if(products ==null){
            return;
        }
        tName.setText(products.getName());
        tCost.setText(getString(R.string.rupee_symbol) + String.valueOf(products.getPrice()));
        Glide.with(this).load(products.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_food)
                .into(iFoodImage);
        tQuantity.setText(String.valueOf(products.getQuantity()));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.i_minus:
                if(duplicateProducts.getQuantity()!=0) {
                    duplicateProducts.setQuantity(duplicateProducts.getQuantity()-1);
                    tQuantity.setText(String.valueOf(duplicateProducts.getQuantity()));
                }
                fishDetailViewModel.updateCart(duplicateProducts);
                break;
            case R.id.i_plus:
                duplicateProducts.setQuantity(duplicateProducts.getQuantity()+1);
                tQuantity.setText(String.valueOf(duplicateProducts.getQuantity()));
                fishDetailViewModel.updateCart(duplicateProducts);
                break;
            case R.id.b_cart:
                startActivity(new Intent(this,CartActivity.class));
                break;

        }
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

    @Override
    protected void onDestroy() {
        fishDetailViewModel.getFoodDetailsLiveData().removeObserver(foodDetailObserver);
        fishDetailViewModel.getCartItemsLiveData().removeObserver(cartObserver);
        super.onDestroy();
    }
}
