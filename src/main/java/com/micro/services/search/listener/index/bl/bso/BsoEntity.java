package com.micro.services.search.listener.index.bl.bso;

public class BsoEntity {

    private long orderCount;
    private double orderAmount;
    private double margin;

    public BsoEntity(long orderCount, double orderAmount, double margin) {
        this.orderCount = orderCount;
        this.orderAmount = orderAmount;
        this.margin = margin;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
