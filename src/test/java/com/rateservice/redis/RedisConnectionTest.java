package com.rateservice.redis;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 1 smoke test: proves the app can talk to the Dockerized Redis instance.
 * Run `docker-compose up -d` before running this test.
 */
class RedisConnectionTest {

    private static RedisConnection redisConnection;

    @BeforeAll
    static void setUp() {
        redisConnection = new RedisConnection();
    }

    @AfterAll
    static void tearDown() {
        redisConnection.close();
    }

    @Test
    void redisIsReachable() {
        assertTrue(redisConnection.isReachable(),
                "Redis should be reachable - did you run `docker-compose up -d`?");
    }

    @Test
    void setAndGetRoundTrip() {
        try (Jedis jedis = redisConnection.getConnection()) {
            String key = "week1:smoke-test";
            String value = "redis-is-alive";

            jedis.set(key, value);
            String result = jedis.get(key);

            assertEquals(value, result);

            jedis.del(key);
        }
    }

    @Test
    void getMissingKeyReturnsNull() {
        try (Jedis jedis = redisConnection.getConnection()) {
            String result = jedis.get("week1:does-not-exist");
            assertNull(result);
        }
    }
}