package com.doodl6.springmvc.service.cache;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

import java.io.Serializable;

/**
 *
 * Created by daixiaoming on 2018/5/6.
 */
public class MemCache {

    private static final MemCachedClient mcc = new MemCachedClient();

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
    }

    public static void set(String key, Serializable value) {
        mcc.set(key, value);
    }

    public static <T> T get(String key, Class<T> type) {
        return (T) mcc.get(key);
    }
}
