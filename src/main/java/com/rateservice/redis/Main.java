package com.rateservice.redis;

import redis.clients.jedis.Jedis;

/**
 * Standalone demo for Friday's walkthrough - run this after `docker-compose up -d`
 * to show Redis connectivity without needing to run the full test suite.
 *
 * Run with: mvn compile exec:java
 */
public class Main {
    public static void main(String[] args) {
        RedisConnection connection = new RedisConnection();

        System.out.println("Pinging Redis...");
        if (!connection.isReachable()) {
            System.out.println("Redis is NOT reachable. Did you run `docker-compose up -d`?");
            connection.close();
            return;
        }
        System.out.println("Redis is reachable.");

        try (Jedis jedis = connection.getConnection()) {
            jedis.set("demo:hello", "world");
            String value = jedis.get("demo:hello");
            System.out.println("SET demo:hello -> world");
            System.out.println("GET demo:hello -> " + value);
            jedis.del("demo:hello");
        }

        connection.close();
    }
}