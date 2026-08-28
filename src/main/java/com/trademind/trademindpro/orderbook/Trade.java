package com.trademind.trademindpro.orderbook;

public class Trade {

    private final String buyOrderId;
    private final String sellOrderId;
    private final double price;
    private final int quantity;

    public Trade(
            String buyOrderId,
            String sellOrderId,
            double price,
            int quantity) {

        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}