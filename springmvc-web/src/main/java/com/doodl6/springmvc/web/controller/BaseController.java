package com.doodl6.springmvc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 抽象控制基类
 */
public abstract class BaseController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    //因为Controller是单例的，所以一定要使用线程变量存储每个请求的request和response

    /**
     * 线程变量-request
     */
    private ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    /**
     * 线程变量-response
     */
    private ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();

    /**
     * 设置Servlet参数
     */
    @ModelAttribute
    private void setServletParams(HttpServletRequest request, HttpServletResponse response) {
        this.request.set(request);
        this.response.set(response);
    }

    /**
     * 获取 请求对象。
     */
    protected HttpServletRequest getRequest() {
        return this.request.get();
    }

    /**
     * 获取响应对象。
     */
    protected HttpServletResponse getResponse() {
        return this.response.get();
    }

}
