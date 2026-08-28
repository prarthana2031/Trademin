
package com.trademind.trademindpro.orderbook;

import com.trademind.trademindpro.trading.order.Order;
import com.trademind.trademindpro.trading.order.OrderSide;

import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    void shouldAddBuyOrdersInDescendingPriceOrder() {

        OrderBook orderBook = new OrderBook();

        Order order1 = new Order(
                "O1",
                OrderSide.BUY,
                100.0,
                10,
                1
        );

        Order order2 = new Order(
                "O2",
                OrderSide.BUY,
                105.0,
                5,
                2
        );

        Order order3 = new Order(
                "O3",
                OrderSide.BUY,
                99.0,
                20,
                3
        );

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);

        assertEquals(105.0, Collections.max(orderBook.getBids().keySet()));
        assertEquals(99.0, Collections.min(orderBook.getBids().keySet()));
    }


    @Test
    void shouldAddSellOrdersInAscendingPriceOrder() {

        OrderBook orderBook = new OrderBook();

        Order order1 = new Order(
                "O1",
                OrderSide.SELL,
                105.0,
                10,
                1
        );

        Order order2 = new Order(
                "O2",
                OrderSide.SELL,
                101.0,
                5,
                2
        );

        Order order3 = new Order(
                "O3",
                OrderSide.SELL,
                110.0,
                20,
                3
        );

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);

        assertEquals(101.0, Collections.min(orderBook.getAsks().keySet()));
        assertEquals(110.0, Collections.max(orderBook.getAsks().keySet()));
    }


    @Test
    void shouldMaintainTimePriorityAtSamePrice() {

        OrderBook orderBook = new OrderBook();

        Order first = new Order(
                "O1",
                OrderSide.BUY,
                100.0,
                10,
                1
        );

        Order second = new Order(
                "O2",
                OrderSide.BUY,
                100.0,
                5,
                2
        );

        orderBook.addOrder(first);
        orderBook.addOrder(second);

        Queue<Order> queue =
                orderBook.getBids().get(100.0);

        assertNotNull(queue);

        assertEquals(
                "O1",
                queue.peek().getOrderId()
        );
    }


    @Test
    void shouldFullyMatchBuyAndSellOrders() {

        OrderBook orderBook = new OrderBook();

        Order sellOrder = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            10,
            1
        );

        Order buyOrder = new Order(
            "B1",
            OrderSide.BUY,
            100.0,
            10,
            2
        );

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        assertTrue(orderBook.getAsks().isEmpty());
   
        assertTrue(orderBook.getBids().isEmpty());

    }  
    @Test
void shouldPartiallyFillSellOrder() {

    OrderBook orderBook = new OrderBook();

    Order sellOrder = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            10,
            1
    );

    Order buyOrder = new Order(
            "B1",
            OrderSide.BUY,
            100.0,
            4,
            2
    );

    orderBook.addOrder(sellOrder);
    orderBook.addOrder(buyOrder);

    assertTrue(orderBook.getBids().isEmpty());

    Queue<Order> remainingSellOrders =
            orderBook.getAsks().get(100.0);

    assertNotNull(remainingSellOrders);

    assertEquals(
            6,
            remainingSellOrders.peek().getQuantity()
    );
}

@Test
void shouldKeepOrdersWhenPricesDoNotMatch() {

    OrderBook orderBook = new OrderBook();

    Order sellOrder = new Order(
            "S1",
            OrderSide.SELL,
            110.0,
            10,
            1
    );

    Order buyOrder = new Order(
            "B1",
            OrderSide.BUY,
            100.0,
            5,
            2
    );

    orderBook.addOrder(sellOrder);
    orderBook.addOrder(buyOrder);

    assertFalse(orderBook.getAsks().isEmpty());
    assertFalse(orderBook.getBids().isEmpty());

    assertEquals(10,
            orderBook.getAsks()
                    .get(110.0)
                    .peek()
                    .getQuantity());

    assertEquals(5,
            orderBook.getBids()
                    .get(100.0)
                    .peek()
                    .getQuantity());
}

@Test
void shouldCreateTradeWhenOrdersMatch() {

    OrderBook orderBook = new OrderBook();

    Order sellOrder = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            10,
            1
    );

    Order buyOrder = new Order(
            "B1",
            OrderSide.BUY,
            105.0,
            4,
            2
    );

    orderBook.addOrder(sellOrder);
    orderBook.addOrder(buyOrder);

    assertEquals(1, orderBook.getTrades().size());

    Trade trade = orderBook.getTrades().get(0);

    assertEquals("B1", trade.getBuyOrderId());
    assertEquals("S1", trade.getSellOrderId());
    assertEquals(100.0, trade.getPrice());
    assertEquals(4, trade.getQuantity());
}







@Test
void shouldMatchMultipleSellOrders() {

    OrderBook orderBook = new OrderBook();

    Order sell1 = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            5,
            1
    );

    Order sell2 = new Order(
            "S2",
            OrderSide.SELL,
            101.0,
            5,
            2
    );

    Order buy = new Order(
            "B1",
            OrderSide.BUY,
            105.0,
            8,
            3
    );

    orderBook.addOrder(sell1);
    orderBook.addOrder(sell2);
    orderBook.addOrder(buy);

    assertTrue(orderBook.getBids().isEmpty());

    Queue<Order> remainingSell =
            orderBook.getAsks().get(101.0);

    assertNotNull(remainingSell);

    assertEquals(
            2,
            remainingSell.peek().getQuantity()
    );
}

@Test
void shouldMatchEarlierOrderFirstAtSamePrice() {

    OrderBook orderBook = new OrderBook();

    Order firstSell = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            5,
            1
    );

    Order secondSell = new Order(
            "S2",
            OrderSide.SELL,
            100.0,
            5,
            2
    );

    Order buy = new Order(
            "B1",
            OrderSide.BUY,
            100.0,
            5,
            3
    );

    orderBook.addOrder(firstSell);
    orderBook.addOrder(secondSell);
    orderBook.addOrder(buy);

    assertEquals(1, orderBook.getTrades().size());

    Trade trade = orderBook.getTrades().get(0);

    assertEquals("S1", trade.getSellOrderId());

    assertEquals(
            5,
            orderBook.getAsks()
                    .get(100.0)
                    .peek()
                    .getQuantity()
    );
}

@Test
void shouldMatchIncomingSellAgainstBestBuy() {

    OrderBook orderBook = new OrderBook();

    Order buy1 = new Order(
            "B1",
            OrderSide.BUY,
            100.0,
            5,
            1
    );

    Order buy2 = new Order(
            "B2",
            OrderSide.BUY,
            105.0,
            5,
            2
    );

    Order sell = new Order(
            "S1",
            OrderSide.SELL,
            100.0,
            3,
            3
    );

    orderBook.addOrder(buy1);
    orderBook.addOrder(buy2);
    orderBook.addOrder(sell);

    assertEquals(1, orderBook.getTrades().size());

    Trade trade = orderBook.getTrades().get(0);

    assertEquals("B2", trade.getBuyOrderId());
    assertEquals("S1", trade.getSellOrderId());

    assertEquals(105.0, trade.getPrice());
    assertEquals(3, trade.getQuantity());

    assertEquals(
            2,
            orderBook.getBids()
                    .get(105.0)
                    .peek()
                    .getQuantity()
    );
}


}