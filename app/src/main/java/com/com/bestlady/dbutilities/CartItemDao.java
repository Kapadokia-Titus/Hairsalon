package com.com.bestlady.dbutilities;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.com.bestlady.model.CartItem;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CartItemDao {
    @Query("SELECT * FROM cartitem")
    LiveData<List<CartItem>> getCartItems();

    @Insert(onConflict = REPLACE)
    void add(CartItem cartItem);

    @Query("DELETE FROM cartitem WHERE item_name = :name")
    void deleteCartItem(String name);

    @Query("DELETE FROM cartitem")
    void deleteCartItems();

    @Query("SELECT quantity FROM cartitem WHERE item_name = :name")
    int getCartCount(String name);
}
