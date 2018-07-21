package com.doodl6.springmvc.service.cache.memcached;

import com.alibaba.fastjson.JSON;
import com.doodl6.springmvc.service.cache.memcached.base.MemCachedService;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * xmemcached实现
 * Created by daixiaoming on 2018/5/12.
 */
@Component
public class XmemcachedImpl implements MemCachedService {

    private static MemcachedClient MEMCACHED_CLIENT = null;

    private static final Transcoder JSON_TRANSCODER = new JSONTranscoder();

    static {
        MemcachedClientBuilder builder =
                new XMemcachedClientBuilder(AddrUtil.
                        getAddresses("127.0.0.1:11211"), new int[]{1});
        try {
            //存储的数据使用JSON格式，兼容不同客户端,如果是单一客户端可以不需要
            builder.setTranscoder(JSON_TRANSCODER);
            MEMCACHED_CLIENT = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String key, Object value) {
        try {
            MEMCACHED_CLIENT.set(key, 1000, value);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        try {
            GetsResponse<String> getsResponse = MEMCACHED_CLIENT.gets(key, JSON_TRANSCODER);
            String value = getsResponse.getValue();
            if (value != null) {
                return JSON.parseObject(value, type);
            }
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        XmemcachedImpl xmemcached = new XmemcachedImpl();
        xmemcached.set("a", "111");
        System.out.println(xmemcached.get("a", String.class));
    }
}
