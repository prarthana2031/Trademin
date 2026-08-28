package com.trademind.trademindpro.trading.order;

public class Order {

    private String orderId;
    private OrderSide side;
    private double price;
    private int quantity;
    private long timestamp;

    public Order(String orderId,
                 OrderSide side,
                 double price,
                 int quantity,
                 long timestamp) {

        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderSide getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }
}