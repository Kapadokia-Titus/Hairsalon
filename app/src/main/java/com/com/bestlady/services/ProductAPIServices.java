package com.com.bestlady.services;

import com.com.bestlady.model.Products;
import com.com.bestlady.model.Orders;
import com.com.bestlady.model.OrdersResponse;
import com.com.bestlady.model.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProductAPIServices {

    @GET("/product/all")
    Call<List<Products>> getFoodData();
    @GET("/product/{creatorId}")
    Call<List<Products>> getMyFishData(@Path("creatorId") String creatorId);
    @POST("/product")
    Call<ProductResponse> submitFishData(@Body Products products);
    @POST("/orders")
    Call<OrdersResponse> submitFishOrder(@Body Orders orders);
    @GET("/orders/{sellerId}")
    Call<List<Orders>> getMyFishOrders(@Path("sellerId") String sellerId);
}
