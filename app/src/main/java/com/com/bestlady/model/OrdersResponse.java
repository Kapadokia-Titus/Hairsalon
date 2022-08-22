package com.com.bestlady.model;

public class OrdersResponse {
    private long id;
    private String status;
    private String message;

    public OrdersResponse() {
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

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
