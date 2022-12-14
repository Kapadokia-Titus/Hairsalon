package com.com.bestlady.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class CartItem {
    @ForeignKey(entity = Products.class,parentColumns = "name",childColumns = "item_name",onDelete = ForeignKey.CASCADE)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "item_name")
    private String name="";

    @ColumnInfo(name = "item_price")
    private Double price;

    @ColumnInfo(name = "quantity")
    private Integer quantity = 1;

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
