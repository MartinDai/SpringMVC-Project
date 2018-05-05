package com.doodl6.springmvc.web.controller;

import com.doodl6.springmvc.common.util.ExcelUtil;
import com.doodl6.springmvc.web.constant.WebConstants;
import com.doodl6.springmvc.web.response.BaseResponse;
import com.doodl6.springmvc.web.response.MapResponse;
import com.doodl6.springmvc.web.util.DownloadUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 首页控制类
 */
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

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
    public void down(HttpServletResponse response) throws IOException {
        HSSFWorkbook hssfWorkbook;
        try {
            hssfWorkbook = ExcelUtil.createHSSFWorkbook(WebConstants.ROOT_PATH + "ddd" + WebConstants.TEMPLATE_PATH);
        } catch (Exception e) {
            throw new IllegalStateException("模板文件未找到");
        }

        String fileName = "模板.xls";
        DownloadUtil.downExcel(response, hssfWorkbook, fileName);
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
        Workbook workbook = ExcelUtil.convertToWorkbook(tempFile);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();

        BaseResponse<String> response = new BaseResponse<>();

        response.setData("文件上传成功，文件名:" + originalFileName + "，有效行数:" + rows);

        return response;
    }

}
