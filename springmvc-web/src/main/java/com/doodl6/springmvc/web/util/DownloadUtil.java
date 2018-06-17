package com.doodl6.springmvc.web.util;

import com.doodl6.springmvc.common.model.RowModel;
import com.doodl6.springmvc.common.util.ExcelUtil;
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
            throw new FileNotFoundException("文件未找到");
        }

        ExcelUtil.fillExcelData(workbook, dataList);

        downExcel(response, workbook, fileName);
    }

    /**
     * 输出excel数据
     */
    public static void downExcel(HttpServletResponse response, HSSFWorkbook workbook, String fileName) throws IOException {
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
            response.addIntHeader("Content-Length", workbook.getBytes().length);
            os.flush();
        }
    }

}
