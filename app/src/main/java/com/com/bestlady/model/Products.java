package com.com.bestlady.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Products {

    @PrimaryKey
    @SerializedName("item_name")
    @Expose
    @NonNull
    private String name;

    @SerializedName("item_price")
    @Expose
    private Double price;

    @SerializedName("average_rating")
    @Expose
    private Double rating;
    @SerializedName("creatorId")
    @Expose
    private String creatorId;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("item_quantity")
    @Expose
    private Integer quantity = 0;

    public Products() {
    }

    public Products(@NonNull String name, Double price, Double rating, String creatorId, String imageUrl, Integer quantity) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.creatorId = creatorId;
    }

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
