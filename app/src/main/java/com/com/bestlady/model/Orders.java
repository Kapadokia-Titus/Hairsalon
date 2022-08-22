package com.com.bestlady.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Orders {

    @PrimaryKey
    @SerializedName("order_id")
    @Expose
    @NonNull
    private  String order_id;
    @SerializedName("name")
    @Expose
    private  String name;
    @SerializedName("quantity")
    @Expose
    private  String quantity;
    @SerializedName("price")
    @Expose
    private  String price;
    @SerializedName("sellerId")
    @Expose
    private  String sellerId;
    @SerializedName("customerId")
    @Expose
    private  String customerId;

    public Orders() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(@NonNull String order_id) {
        this.order_id = order_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
