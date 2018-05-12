package com.doodl6.springmvc.service.cache.memcached;

import com.alibaba.fastjson.JSON;
import com.doodl6.springmvc.service.cache.memcached.base.MemCachedService;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.springframework.stereotype.Component;

/**
 * Memcached-Java-Client实现
 * Created by daixiaoming on 2018/5/6.
 */
@Component
public class MemCachedImpl implements MemCachedService {

    private static final MemCachedClient MEM_CACHED_CLIENT = new MemCachedClient();

    static {
        String[] servers =
                {
                        "127.0.0.1:11211"
                };

        Integer[] weights = {1};

        // grab an instance of our connection pool
        SockIOPool pool = SockIOPool.getInstance();

        // set the servers and the weights
        pool.setServers(servers);
        pool.setWeights(weights);

        // set some basic pool settings
        // 5 initial, 5 min, and 250 max conns
        // and set the max idle time for a conn
        // to 6 hours
        pool.setInitConn(5);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaxIdle(1000 * 60 * 60 * 6);

        // set the sleep for the maint thread
        // it will wake up every x seconds and
        // maintain the pool size
        pool.setMaintSleep(30);

        // set some TCP settings
        // disable nagle
        // set the read timeout to 3 secs
        // and don't set a connect timeout
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setSocketConnectTO(0);

        // initialize the connection pool
        pool.initialize();

        //存储的数据使用JSON格式，兼容不同客户端,如果是单一客户端可以不需要
        MEM_CACHED_CLIENT.setTransCoder(new JSONObjectTransCoder());
    }

    @Override
    public void set(String key, Object value) {
        MEM_CACHED_CLIENT.set(key, value);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String value = (String) MEM_CACHED_CLIENT.get(key);
        if (value != null) {
            return JSON.parseObject(value, type);
        }
        return null;
    }
}
