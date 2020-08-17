package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.service.config.NacosConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 配置控制类
 */
@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {

    @Resource
    private NacosConfigService nacosConfigService;

    @GetMapping(value = "/getMaxPageSize")
    public int getMaxPageSize() {
        return nacosConfigService.getMaxPageSize();
    }
}
