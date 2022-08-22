package com.com.bestlady.model;

public class ProductResponse {
    private long id;
    private String status;
    private String message;

    public ProductResponse() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}