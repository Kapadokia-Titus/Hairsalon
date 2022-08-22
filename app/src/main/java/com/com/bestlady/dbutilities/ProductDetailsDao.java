package com.com.bestlady.dbutilities;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.com.bestlady.model.Products;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductDetailsDao {

    @Insert(onConflict = REPLACE)
    void save(List<Products> fishDetails);

    @Insert(onConflict = REPLACE)
    void save(Products products);

    @Query("DELETE FROM Products WHERE name NOT IN (:nameList)")
    void deleteOtherFoods(List<String> nameList);

    @Query("DELETE FROM Products")
    void deleteAll();

    @Query("SELECT * FROM Products ORDER BY price ASC")
    LiveData<List<Products>> getFoodsByPrice();

    @Query("SELECT * FROM Products ORDER BY rating DESC")
    LiveData<List<Products>> getFoodsByRating();

    @Query("SELECT * FROM Products WHERE name = :name")
    LiveData<Products> getFood(String name);

    @Query("SELECT * FROM Products WHERE creatorId = :creatorId")
    LiveData<List<Products>> getFishByCreator(String creatorId);
}
