package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.client.api.FirstDubboService;
import com.doodl6.springmvc.client.request.GetDubboInfoRequest;
import com.doodl6.springmvc.client.response.GetDubboInfoResponse;
import com.doodl6.springmvc.common.excel.*;
import com.doodl6.springmvc.web.constant.WebConstants;
import com.doodl6.springmvc.web.response.BaseResponse;
import com.doodl6.springmvc.web.response.MapResponse;
import com.doodl6.springmvc.web.response.ResponseCode;
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

    /**
     * Excel表头
     */
    public static final String[] EXCEL_HEAD = {"第一列:column1", "第一列:column2", "第三列:mergeColumn"};

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
     * 下载接口
     */
    @RequestMapping("down")
    public void down(HttpServletResponse response) {
        try {
            ExcelVersion excelVersion = ExcelVersion.XLS;
            SheetModel sheetModel = new SheetModel();
            sheetModel.setName("测试名称");
            sheetModel.setTitle("测试标题");
            List<Header> headers = ExcelHelper.createHeaders(EXCEL_HEAD);
            sheetModel.setHeaders(headers);

            ExcelVo excelVo = new ExcelVo();
            List<String> cellList = Lists.newArrayList();
            cellList.add("第一列第一行");
            cellList.add("第一列第二行");
            excelVo.setColumn1(cellList);

            cellList = Lists.newArrayList();
            cellList.add("第二列第一行");
            cellList.add("第二列第二行");
            excelVo.setColumn2(cellList);

            excelVo.setMergeColumn("第三列合并行");

            List<ExcelVo> voList = Lists.newArrayList();
            voList.add(excelVo);
            List<ExcelData> dataList = ExcelHelper.parseExcelDataList(voList);

            sheetModel.setDataList(dataList);

            List<SheetModel> sheets = Lists.newArrayList();
            sheets.add(sheetModel);
            ExcelModel excelModel = new ExcelModel(excelVersion, sheets);
            Workbook workbook = excelModel.generateWorkbook();
            ResponseUtil.responseExcel(response, workbook, "模板." + excelVersion.getSuffix());
        } catch (Exception e) {
            throw new IllegalStateException("下载出现异常");
        }
    }

    /**
     * 上传接口
     */
    @RequestMapping("upload")
    public BaseResponse<String> upload(@RequestParam CommonsMultipartFile template) throws IOException, InvalidFormatException {
        String originalFileName = template.getOriginalFilename();
        if (!originalFileName.endsWith(".xls")) {
            throw new IllegalArgumentException("请把文件另存为xls或Excel97-2004格式再上传(不能直接修改文件扩展名)");
        }

        String tempFileName = System.currentTimeMillis() + ".xls";
        File tempFile = new File(WebConstants.ROOT_PATH + "/tmp/", tempFileName);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }

        //保存临时文件
        template.transferTo(tempFile);
        Workbook workbook = WorkbookFactory.create(tempFile);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();

        BaseResponse<String> response = new BaseResponse<>();

        response.setData("文件上传成功，文件名:" + originalFileName + "，有效行数:" + rows);

        return response;
    }

    /**
     * 获取dubbo信息
     */
    @RequestMapping("/getDubboInfo")
    public MapResponse getDubboInfo(Long id) {
        MapResponse mapResponse = new MapResponse();

        GetDubboInfoRequest getDubboInfoRequest = new GetDubboInfoRequest();
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
