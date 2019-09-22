package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.client.api.FirstDubboService;
import com.doodl6.springmvc.client.request.GetDubboInfoRequest;
import com.doodl6.springmvc.client.response.GetDubboInfoResponse;
import com.doodl6.springmvc.common.check.CheckUtil;
import com.doodl6.springmvc.common.excel.*;
import com.doodl6.springmvc.web.aspect.TraceIdHolder;
import com.doodl6.springmvc.web.constant.WebConstants;
import com.doodl6.springmvc.web.request.CheckParameterRequest;
import com.doodl6.springmvc.web.response.CheckParameterResult;
import com.doodl6.springmvc.web.response.base.BaseResponse;
import com.doodl6.springmvc.web.response.base.MapResponse;
import com.doodl6.springmvc.web.response.base.ResponseCode;
import com.doodl6.springmvc.web.util.ResponseUtil;
import com.doodl6.springmvc.web.vo.ExcelVo;
import com.google.common.collect.Lists;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 首页控制类
 */
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Autowired(required = false)
    private FirstDubboService firstDubboService;

    /**
     * 普通接口
     */
    @RequestMapping("/hello")
    public MapResponse hello(String name) {
        MapResponse mapResponse = new MapResponse();

        mapResponse.appendData("name", name);

        return mapResponse;
    }

    /**
     * 获取dubbo信息
     */
    @RequestMapping("/getDubboInfo")
    public MapResponse getDubboInfo(Long id) {
        MapResponse mapResponse = new MapResponse();

        GetDubboInfoRequest getDubboInfoRequest = new GetDubboInfoRequest();
        getDubboInfoRequest.setTraceId(TraceIdHolder.getTraceId());
        getDubboInfoRequest.setId(id);
        GetDubboInfoResponse getDubboInfoResponse = firstDubboService.getDubboInfo(getDubboInfoRequest);
        if (getDubboInfoResponse.isSuccess()) {
            mapResponse.appendData("dubboInfo", getDubboInfoResponse.getDubboDomain());
        } else {
            mapResponse.setResult(ResponseCode.BIZ_ERROR);
            mapResponse.setMessage(getDubboInfoResponse.getErrorMsg());
        }

        return mapResponse;
    }

    /**
     * 参数校验
     */
    @RequestMapping("/parameterCheck")
    public BaseResponse<CheckParameterResult> parameterCheck(CheckParameterRequest request) {

        CheckUtil.check(request);

        CheckParameterResult result = new CheckParameterResult();
        result.setName(request.getName());
        result.setAge(request.getAge());
        result.setFavorites(request.getFavorites());
        BaseResponse<CheckParameterResult> response = new BaseResponse<>();
        response.setData(result);

        return response;
    }

    /**
     * 测试打印日志
     */
    @RequestMapping("/testLog")
    public BaseResponse<Void> testLog() {
        BaseResponse<Void> response = new BaseResponse<>();

        LOGGER.trace("test trace log");
        LOGGER.debug("test debug log");
        LOGGER.info("test info log");
        LOGGER.warn("test warn log");
        LOGGER.error("test error log");

        return response;
    }

}
