package com.rateservice.redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Wraps a Jedis connection pool to the Redis instance defined in docker-compose.yml.
 *
 * Week 1 scope: prove connectivity only. No rate-limiting logic lives here yet -
 * that starts in Week 2 once the Lua script for Token Bucket gets written
 * (see redis-atomicity-writeup.md for why it has to be Lua and not plain GET/INCR).
 */
public class RedisConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6379;

    private final JedisPool jedisPool;

    public RedisConnection() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public RedisConnection(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);

        this.jedisPool = new JedisPool(poolConfig, host, port);
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    /** Simple liveness check - returns true if Redis responds to PING. */
    public boolean isReachable() {
        try (Jedis jedis = getConnection()) {
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        jedisPool.close();
    }
}