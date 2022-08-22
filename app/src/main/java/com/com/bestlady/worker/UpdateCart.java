package com.com.bestlady.worker;

import android.os.AsyncTask;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;


public class UpdateCart extends AsyncTask<Products, Void, Void> {
    private AppDatabase db;
    public UpdateCart(AppDatabase db) {
        this.db = db;
    }

    @Override
    protected Void doInBackground(Products... productDetails) {
        if(db!=null){
            if(productDetails[0]!=null) {
                if (productDetails[0].getQuantity() == 0) {
                    db.cartItemDao().deleteCartItem(productDetails[0].getName());
                    return null;
                }
                CartItem cartItem = new CartItem();
                cartItem.setName(productDetails[0].getName());
                cartItem.setPrice(productDetails[0].getPrice());
                cartItem.setQuantity(productDetails[0].getQuantity());
                db.cartItemDao().add(cartItem);
            }
        }
        return null;
    }
}
