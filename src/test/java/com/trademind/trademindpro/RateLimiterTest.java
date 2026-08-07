package com.trademind.trademindpro;

import com.trademind.trademindpro.ratelimiter.service.RateLimiter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterTest {

    @Test
    void firstFiveRequestsShouldBeAllowed() {

        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 1; i <= 5; i++) {
            assertTrue(rateLimiter.allowRequest("Nandini"));
        }
    }

    @Test
    void sixthRequestShouldBeRejected() {

        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 1; i <= 5; i++) {
            rateLimiter.allowRequest("Nandini");
        }

        assertFalse(rateLimiter.allowRequest("Nandini"));
    }

    @Test
    void differentClientsShouldHaveSeparateLimits() {

        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 1; i <= 5; i++) {
            assertTrue(rateLimiter.allowRequest("Nandini"));
        }

        assertTrue(rateLimiter.allowRequest("Rahul"));
        assertFalse(rateLimiter.allowRequest("Nandini"));
    }

    @Test
    void newClientShouldAlwaysBeAllowed() {

        RateLimiter rateLimiter = new RateLimiter();

        assertTrue(rateLimiter.allowRequest("A"));
        assertTrue(rateLimiter.allowRequest("B"));
        assertTrue(rateLimiter.allowRequest("C"));
    }

    @Test
    void counterShouldResetAfterWindowExpires() throws InterruptedException {

        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 1; i <= 5; i++) {
            rateLimiter.allowRequest("Nandini");
        }

        assertFalse(rateLimiter.allowRequest("Nandini"));

        Thread.sleep(61000);

        assertTrue(rateLimiter.allowRequest("Nandini"));
    }
}