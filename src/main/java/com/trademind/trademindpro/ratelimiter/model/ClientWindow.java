package com.trademind.trademindpro.ratelimiter.model;

public class ClientWindow {

    private long windowStartTime;
    private int requestCount;

    // Constructor
    public ClientWindow(long windowStartTime, int requestCount) {
        this.windowStartTime = windowStartTime;
        this.requestCount = requestCount;
    }

    // Getter
    public long getWindowStartTime() {
        return windowStartTime;
    }

    // Setter
    public void setWindowStartTime(long windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    // Getter
    public int getRequestCount() {
        return requestCount;
    }

    // Setter
    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
}