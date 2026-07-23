package com.trademind.trademindpro.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class RateLimiter {

    private final int LIMIT = 5;
    private final long WINDOW_SIZE = 60000; // 60 seconds

    private final Map<String, ClientWindow> clients = new ConcurrentHashMap<>();

    public boolean allowRequest(String clientId) {

        long currentTime = System.currentTimeMillis();

        // New Client
        if (!clients.containsKey(clientId)) {

            clients.put(clientId, new ClientWindow(currentTime, 1));

            return true;
        }

        ClientWindow client = clients.get(clientId);

        // Window Expired
        if (currentTime - client.getWindowStartTime() >= WINDOW_SIZE) {

            client.setWindowStartTime(currentTime);
            client.setRequestCount(1);

            return true;
        }

        // Within Current Window
        if (client.getRequestCount() < LIMIT) {

            client.setRequestCount(client.getRequestCount() + 1);

            return true;
        }

        // Limit Reached
        return false;
    }
}

class ClientWindow {
    private long windowStartTime;
    private int requestCount;

    public ClientWindow(long windowStartTime, int requestCount) {
        this.windowStartTime = windowStartTime;
        this.requestCount = requestCount;
    }

    public long getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(long windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
}