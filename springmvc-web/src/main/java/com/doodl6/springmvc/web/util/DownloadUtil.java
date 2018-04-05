package com.doodl6.springmvc.web.util;

import com.doodl6.springmvc.common.model.RowModel;
import com.doodl6.springmvc.common.util.ExcelUtil;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 下载工具类
 */
public class DownloadUtil {

    /**
     * 输出excel数据
     */
    public static void downExcel(HttpServletResponse response, String fileName, String templatePath, List<RowModel> dataList) throws IOException {
        HSSFWorkbook workbook;
        try {
            workbook = ExcelUtil.createHSSFWorkbook(templatePath);
        } catch (Exception e) {
            throw new FileNotFoundException("模板文件未找到");
        }

        ExcelUtil.fillExcelData(workbook, dataList);

        downExcel(response, workbook, fileName);
    }

    /**
     * 输出excel数据
     */
    private static void downExcel(HttpServletResponse response, HSSFWorkbook workbook, String fileName) throws IOException {
        OutputStream os = null;
        try {
            response.reset();
            // 编码处理，对于操作系统 （ linux默认 utf-8,windows 默认 GBK)
            String defaultEncoding = System.getProperty("file.encoding");
            if (defaultEncoding != null && defaultEncoding.equals("UTF-8")) {
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
            }

            // 设置response的Header
            response.setContentType("application/vnd.ms-excel");

            os = response.getOutputStream();
            workbook.write(os);
            long len = workbook.getBytes().length;
            response.addHeader("Content-Length", String.valueOf(len));
            os.flush();
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

}
