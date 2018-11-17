package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.service.cache.Model;
import com.doodl6.springmvc.service.cache.memcached.base.MemCachedService;
import com.doodl6.springmvc.service.cache.redis.RedisService;
import com.doodl6.springmvc.web.response.base.BaseResponse;
import com.doodl6.springmvc.web.response.base.MapResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 缓存控制类
 */
@RestController
@RequestMapping("/cache")
public class CacheController extends BaseController {

    @Resource
    private MemCachedService memCachedImpl;

    @Resource
    private MemCachedService xmemcachedImpl;

    @Resource
    private RedisService redisService;

    /**
     * 设置缓存数据到memcached
     */
    @RequestMapping("/setToMemcached")
    public BaseResponse<Map<String, Object>> setToMemcached(String key, String value) {
        MapResponse mapResponse = new MapResponse();

        Model model = new Model(key, value);
        xmemcachedImpl.set(key, model);
        model = xmemcachedImpl.get(key, Model.class);
        mapResponse.appendData("key", model);

        return mapResponse;
    }

    /**
     * 从memcached获取缓存数据
     */
    @RequestMapping("/getFromMemcached")
    public MapResponse getFromMemcached(String key) {
        MapResponse mapResponse = new MapResponse();

        Model value = memCachedImpl.get(key, Model.class);
        mapResponse.appendData("key", value);

        return mapResponse;
    }

    /**
     * 设置缓存数据到redis
     */
    @RequestMapping("/setToRedis")
    public BaseResponse<Map<String, Object>> setToRedis(String key, String value) {
        MapResponse mapResponse = new MapResponse();

        Model model = new Model(key, value);
        redisService.set(key, model);
        model = redisService.get(key, Model.class);
        mapResponse.appendData("key", model);

        return mapResponse;
    }

    /**
     * 从redis获取缓存数据
     */
    @RequestMapping("/getFromRedis")
    public MapResponse getFromRedis(String key) {
        MapResponse mapResponse = new MapResponse();

        Model value = redisService.get(key, Model.class);
        mapResponse.appendData("key", value);

        return mapResponse;
    }

}
