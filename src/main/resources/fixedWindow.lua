local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])

local current = redis.call("GET", key)

if not current then
    redis.call("SET", key, 1)
    redis.call("EXPIRE", key, window)
    return 1
end

current = tonumber(current)

if current < limit then
    redis.call("INCR", key)
    return current + 1
end

return -1