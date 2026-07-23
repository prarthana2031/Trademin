# Why Rate Limiting Needs Atomic Operations (Redis + Lua)

## The problem: read-check-increment isn't atomic

Naive rate limiter logic looks like this:

```
count = GET(key)
if count < limit:
    INCREMENT(key)
    allow request
else:
    reject request
```

This works fine single-threaded. It breaks the moment requests arrive concurrently — which is the entire point of a rate limiter sitting behind a gateway.

**Race condition walkthrough** (limit = 5, current count = 4):

1. Request A reads count → `4` (4 < 5, looks fine)
2. Request B reads count → `4` (before A's increment has landed — B also sees 4 < 5)
3. Request A increments → count is now `5`, A is allowed
4. Request B increments → count is now `6`, B is allowed too

Two requests get allowed when only one should have been. GET and INCR are two separate round trips to Redis, and anything can interleave between them. At production traffic, the gap between those two calls is sub-millisecond, but sub-millisecond windows are exactly what happens constantly under load — this is precisely the kind of bug Week 8's K6 load tests are designed to surface.

## The solution: run it as one atomic operation with Lua

Redis lets the app send a Lua script to the server with `EVAL`. Redis guarantees the entire script runs atomically — no other command can interleave in the middle of it, because Redis executes commands (and whole `EVAL` scripts) single-threaded.

Instead of two round trips from the app (GET, then maybe INCR), the app sends one script that does read-check-increment entirely on the Redis server:

```lua
-- sketch only — the real version gets built in Week 2 for Token Bucket
local current = tonumber(redis.call('GET', KEYS[1]) or "0")
if current < tonumber(ARGV[1]) then
    redis.call('INCR', KEYS[1])
    return 1  -- allowed
else
    return 0  -- rejected
end
```

Because the whole block runs as one atomic unit on the server, there's no window where two requests can both read the same count before either writes. The race condition above becomes impossible — not "less likely," impossible — because Redis never interleaves two `EVAL` calls against the same script.

## What this means for Week 2

Token Bucket needs to atomically: read the current token count + last refill timestamp, calculate how many tokens should have refilled since then, check if a token is available, and decrement if so. That's more state than the sketch above, but it's the same pattern — one `EVAL` call, one atomic unit, no race window.

This doc is the reference for that implementation. The mechanism doesn't change between now and Week 2 — just the amount of state the script has to juggle.

## Quick reference for Friday's demo

- `docker-compose up -d` — start Redis
- `mvn compile exec:java` (from `redis-client/`) — run the connectivity demo
- `mvn test` — run the SET/GET smoke tests