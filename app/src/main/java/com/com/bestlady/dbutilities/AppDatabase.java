package com.com.bestlady.dbutilities;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.com.bestlady.model.CartItem;
import com.com.bestlady.model.Products;
import com.com.bestlady.model.Orders;


@Database(entities = {Products.class, CartItem.class, Orders.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ProductDetailsDao foodDetailsDao();
    public abstract CartItemDao cartItemDao();
    public abstract OrderDao orderDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"fishes").fallbackToDestructiveMigration().allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
