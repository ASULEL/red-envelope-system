if (redis.call('exists', KEYS[1]) == 1 and redis.call('exists', KEYS[2]) == 1) then
    local stock = tonumber(redis.call('get', KEYS[1]));
    if (stock > 0) then
        redis.call('incrby', KEYS[1], -1);
        local money = tonumber(redis.call('rpop', KEYS[2]));
        return { stock, money };
    end ;
    return { 0, 0 };
else
    return { 0, 0 };
end ;
