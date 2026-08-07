package com.trademind.trademindpro.ratelimiter.config;

public class LimitRule {

    private int limit;
    private int window;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }
}