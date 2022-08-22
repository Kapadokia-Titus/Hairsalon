package com.com.bestlady.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.com.bestlady.R;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Orders;
import com.com.bestlady.model.OrdersResponse;
import com.com.bestlady.ui.adapters.CartListAdapter;
import com.com.bestlady.utility.PreferenceKeys;
import com.com.bestlady.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CartActivity";
    RecyclerView cartList;
    TextView tDiscount,hDiscount,tItemsCost,tDelivery,hDelivery,tGrandTotal;
    TextInputEditText eCoupon;
    TextInputLayout eCouponLayout;
    AppCompatButton bApply;
    ImageView iRemoveCoupon;
    CartViewModel cartViewModel;
    Observer<List<CartItem>> cartObserver;
    Observer<Double> costObserver;
    Observer<String> errorObserver;
    CartListAdapter cartListAdapter;
    int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle(R.string.your_cart);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cartList = findViewById(R.id.cart_list);
        tDiscount = findViewById(R.id.t_discount);
        tItemsCost = findViewById(R.id.t_total);
        hDiscount = findViewById(R.id.h_discount);
        tDelivery = findViewById(R.id.t_delivery);
        hDelivery = findViewById(R.id.h_delivery);
        iRemoveCoupon = findViewById(R.id.i_remove);
        tGrandTotal = findViewById(R.id.t_grand_total);
        eCouponLayout = findViewById(R.id.coupon_layout);
        eCoupon = findViewById(R.id.e_coupon);
        bApply = findViewById(R.id.b_apply);
        bApply.setOnClickListener(this);
        iRemoveCoupon.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        cartList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        cartObserver = new Observer<List<CartItem>>() {
            @Override
            public void onChanged(@Nullable List<CartItem> cartItems) {
                cartListAdapter.setData(cartItems);
                cartListAdapter.notifyDataSetChanged();
                count = cartItems.size();
                if(cartItems!=null && cartItems.size()==0){
                    finish();
                }
            }
        };
        costObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double aDouble) {
                updateUI(aDouble);
            }
        };
        errorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String error) {
                if(error!=null && error.isEmpty()){
                    eCouponLayout.setError(null);
                    eCouponLayout.setErrorEnabled(false);
                }else{
                    eCouponLayout.setError(error);
                    eCouponLayout.setErrorEnabled(true);
                }
            }
        };
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        cartListAdapter = new CartListAdapter(new ArrayList<CartItem>(),cartViewModel);
        cartList.setAdapter(cartListAdapter);
        cartViewModel.getCartItemsLiveData().observe(this,cartObserver);
        cartViewModel.getGrandTotal().observe(this,costObserver);
        cartViewModel.getErrorString().observe(this,errorObserver);
        eCoupon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    applyCoupon();
                    return false;
                }
                return false;
            }
        });
    }

    private void updateUI(Double grandTotal) {
        tItemsCost.setText(getString(R.string.rupee_symbol)+" "+cartViewModel.getTotalCost());
        tGrandTotal.setText(getString(R.string.rupee_symbol)+" "+ String.valueOf(grandTotal));
        if(cartViewModel.getDiscountAmt()>0){
            hDiscount.setVisibility(View.VISIBLE);
            tDiscount.setVisibility(View.VISIBLE);
            hDiscount.setText(getString(R.string.discount)+" ( 20% )");
            tDiscount.setText(" - "+getString(R.string.rupee_symbol)+" "+ String.valueOf(cartViewModel.getDiscountAmt()));
        }else{
            hDiscount.setVisibility(View.GONE);
            tDiscount.setVisibility(View.GONE);
        }
        if(cartViewModel.getDeliveryCost()>0){
            hDelivery.setText(getString(R.string.delivery_charges));
            tDelivery.setText(" + "+getString(R.string.rupee_symbol)+" "+ String.valueOf(cartViewModel.getDeliveryCost()));
            tDelivery.setPaintFlags(0);
        }else{
            hDelivery.setText(getString(R.string.delivery_charges)+" ( Free )");
            tDelivery.setText(" + "+getString(R.string.rupee_symbol)+" 30.00");
            tDelivery.setPaintFlags(tDelivery.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.b_apply){
            applyCoupon();
        }else if(view.getId() == R.id.i_remove){
            eCoupon.setFocusable(true);
            eCoupon.setFocusableInTouchMode(true);
            eCoupon.setText("");
            eCoupon.setLongClickable(true);
            eCouponLayout.setErrorEnabled(false);
            eCouponLayout.setError(null);
            iRemoveCoupon.setVisibility(View.INVISIBLE);
            bApply.setVisibility(View.VISIBLE);
            cartViewModel.applyCoupon("");
        }
    }

    private void applyCoupon() {
        if(eCoupon.getText()!=null) {
            String coupon = eCoupon.getText().toString().trim().toUpperCase();
            if (!coupon.isEmpty() && (coupon.equals("FREEDEL") || coupon.equals("F22LABS"))) {
                eCouponLayout.setErrorEnabled(false);
                eCouponLayout.setError(null);
                cartViewModel.applyCoupon(coupon);
                eCoupon.setFocusable(false);
                eCoupon.setFocusableInTouchMode(false);
                eCoupon.setLongClickable(false);
                iRemoveCoupon.setVisibility(View.VISIBLE);
                bApply.setVisibility(View.INVISIBLE);
            } else {
                eCouponLayout.setErrorEnabled(true);
                eCouponLayout.setError("coupon not valid");
            }
        }
    }

    @Override
    protected void onDestroy() {
        cartViewModel.getCartItemsLiveData().removeObserver(cartObserver);
        cartViewModel.getGrandTotal().removeObserver(costObserver);
        cartViewModel.getErrorString().removeObserver(errorObserver);
        super.onDestroy();
    }

    public void makePayment(View view) {
        Orders orders = new Orders();
        Log.d(TAG, "makePayment: REACHED HERE ");
        //simulating orders
        orders.setCustomerId(String.valueOf(PreferenceKeys.CUSTOMER_ID));
        orders.setSellerId(String.valueOf(PreferenceKeys.SELLER_ID));
        orders.setName("Fish Order");
        orders.setPrice(tGrandTotal.getText().toString().trim());
        orders.setQuantity(String.valueOf(count));
        // get submission status
        OrdersResponse ordersResponse = cartViewModel.submitOrders(orders);
        if (ordersResponse != null) {
            Log.d(TAG, "makePayment: not empty "+ ordersResponse.getMessage());
            Toast.makeText(this, "Order Submitted Successfully", Toast.LENGTH_LONG).show();
            cartViewModel.removeItems();
            startActivity(new Intent(CartActivity.this, HomeScreenActivity.class));
        }else{
            Log.d(TAG, "makePayment: oops!");
            Toast.makeText(this, "Wait Your order will take long to submit", Toast.LENGTH_LONG).show();
        }
    }
}
