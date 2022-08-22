package com.com.bestlady.dbutilities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.com.bestlady.model.Products;
import com.com.bestlady.model.Orders;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderDao {

    @Insert(onConflict = REPLACE)
    void save(List<Orders> orders);

    @Insert(onConflict = REPLACE)
    void save(Orders orders);

    @Query("DELETE FROM Orders WHERE name NOT IN (:nameList)")
    void deleteOtherOrders(List<String> nameList);

    @Query("DELETE FROM Orders")
    void deleteAll();

    @Query("SELECT * FROM Orders ORDER BY price ASC")
    LiveData<List<Products>> getAllOrdersByPrice();


    @Query("SELECT * FROM Orders WHERE name = :name")
    LiveData<Orders> getAllOrders(String name);

    @Query("SELECT * FROM Orders WHERE sellerId = :sellerId")
    LiveData<List<Orders>> getOrdersByCreator(String sellerId);
}
