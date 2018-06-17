package com.doodl6.springmvc.service.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisService {

    private static RedisCommands<String, Object> REDIS_COMMANDS;

    static {
        RedisClient CLIENT = RedisClient.create(RedisURI.create("127.0.0.1", 6379));
        //设置超时时间为10秒
        Duration duration = Duration.ofSeconds(10);
        CLIENT.setDefaultTimeout(duration);
        RedisCodec<String, Object> objectRedisCodec = new SerializedObjectCodec();

        StatefulRedisConnection<String, Object> connect = CLIENT.connect(objectRedisCodec);
        REDIS_COMMANDS = connect.sync();
    }

    public void set(String key, Object value) {
        REDIS_COMMANDS.set(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return (T) REDIS_COMMANDS.get(key);
    }
}
