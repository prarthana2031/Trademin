package com.trademind.trademindpro.orderbook;

import com.trademind.trademindpro.trading.order.Order;
import com.trademind.trademindpro.trading.order.OrderSide;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import java.util.List;

public class OrderBook {

    // BUY orders.
    // Highest price gets priority.
    private final NavigableMap<Double, Queue<Order>> bids =
            new TreeMap<>(Collections.reverseOrder());

    // SELL orders.
    // Lowest price gets priority.
    private final NavigableMap<Double, Queue<Order>> asks =
            new TreeMap<>();

    private final List<Trade> trades = new ArrayList<>();        

    public void addOrder(Order order) {

        matchOrder(order);

        if (order.getQuantity() > 0) {

            if (order.getSide() == OrderSide.BUY) {

                bids.computeIfAbsent(
                        order.getPrice(),
                        price -> new ConcurrentLinkedQueue<>()
                ).add(order);

            } else {

                asks.computeIfAbsent(
                        order.getPrice(),
                        price -> new ConcurrentLinkedQueue<>()
                ).add(order);
            }
        }
    }


    private void matchOrder(Order incomingOrder) {

        if (incomingOrder.getSide() == OrderSide.BUY) {

            while (incomingOrder.getQuantity() > 0 && !asks.isEmpty()) {

                double bestAskPrice = asks.firstKey();

                // No price match
                if (incomingOrder.getPrice() < bestAskPrice) {
                    break;
                }

                Queue<Order> sellQueue = asks.get(bestAskPrice);

                Order sellOrder = sellQueue.peek();

                int tradeQuantity = Math.min(
                        incomingOrder.getQuantity(),
                        sellOrder.getQuantity()
                );

                trades.add(
        new Trade(
                incomingOrder.getOrderId(),
                sellOrder.getOrderId(),
                sellOrder.getPrice(),
                tradeQuantity
        )
);

                incomingOrder.reduceQuantity(tradeQuantity);
                sellOrder.reduceQuantity(tradeQuantity);

                if (sellOrder.getQuantity() == 0) {
                    sellQueue.poll();
                }

                if (sellQueue.isEmpty()) {
                    asks.remove(bestAskPrice);
                }
            }

        } else {

            while (incomingOrder.getQuantity() > 0 && !bids.isEmpty()) {

                double bestBidPrice = bids.firstKey();

                // No price match
                if (incomingOrder.getPrice() > bestBidPrice) {
                    break;
                }

                Queue<Order> buyQueue = bids.get(bestBidPrice);

                Order buyOrder = buyQueue.peek();

                int tradeQuantity = Math.min(
                        incomingOrder.getQuantity(),
                        buyOrder.getQuantity()
                );

                trades.add(
        new Trade(
                buyOrder.getOrderId(),
                incomingOrder.getOrderId(),
                buyOrder.getPrice(),
                tradeQuantity
        )
);

                incomingOrder.reduceQuantity(tradeQuantity);
                buyOrder.reduceQuantity(tradeQuantity);

                if (buyOrder.getQuantity() == 0) {
                    buyQueue.poll();
                }

                if (buyQueue.isEmpty()) {
                    bids.remove(bestBidPrice);
                }
            }
        }
    }


    public NavigableMap<Double, Queue<Order>> getBids() {
        return bids;
    }


    public NavigableMap<Double, Queue<Order>> getAsks() {
        return asks;
    }

    public List<Trade> getTrades() {
    return trades;
}
}