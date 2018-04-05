package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.web.response.MapResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制类
 */
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @RequestMapping("/hello")
    public MapResponse hello(String name) throws Exception {
        MapResponse mapResponse = new MapResponse();

        mapResponse.appendData("name", name);

        return mapResponse;
    }

}
