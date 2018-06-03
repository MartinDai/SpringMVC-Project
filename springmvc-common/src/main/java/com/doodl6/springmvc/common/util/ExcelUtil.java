package com.doodl6.springmvc.common.util;

import com.doodl6.springmvc.common.model.RowModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Excel工具类
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 根据模板创建Excel对象
     */
    public static HSSFWorkbook createHSSFWorkbook(String templatePath) throws IOException {
        try (InputStream is = new FileInputStream(templatePath)) {
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
            return new HSSFWorkbook(poifsFileSystem);
        }
    }

    /**
     * 把文件转换成excel对象
     */
    public static Workbook convertToWorkbook(File tempFile) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(tempFile);
    }

    /**
     * 填充excel数据
     */
    public static void fillExcelData(HSSFWorkbook workbook, List<RowModel> dataList) {
        if (workbook == null) {
            return;
        }

        //读取excel模板
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowIndex = 1;
        for (RowModel rowModel : dataList) {

            List<List<String>> multiCellList = rowModel.getMultiCellList();
            if (CollectionUtils.isEmpty(multiCellList)) {  //没有多行合并的情况
                //处理每一行
                HSSFRow row = sheet.createRow(rowIndex);
                int cellIndex = 0;
                for (String value : rowModel.getCellList()) {
                    HSSFCell cell = row.createCell(cellIndex);
                    cell.setCellValue(value);
                    cellIndex++;
                }
                rowIndex++;
            } else {  //处理存在多行合并的情况
                //处理每一行
                int cellIndex;
                int multiHeightSize = multiCellList.size();
                List<String> cellValList = rowModel.getCellList();
                int firstPartSize = CollectionUtils.isNotEmpty(cellValList) ? cellValList.size() : 0;
                //先处理多行
                HSSFRow row;
                for (List<String> rowDataList : multiCellList) {
                    row = sheet.createRow(rowIndex);
                    //处理每一行
                    cellIndex = firstPartSize;
                    for (String value : rowDataList) {
                        HSSFCell cell = row.createCell(cellIndex);
                        cell.setCellValue(value);
                        cellIndex++;
                    }
                    rowIndex++;
                }

                if (CollectionUtils.isNotEmpty(cellValList)) {
                    //再处理单行
                    cellIndex = 0;
                    int startRowIndex = rowIndex - multiHeightSize;
                    HSSFRow startRow = sheet.getRow(startRowIndex);
                    for (String value : cellValList) {
                        HSSFCell cell = startRow.createCell(cellIndex);
                        cell.setCellValue(value);
                        sheet.addMergedRegion(new CellRangeAddress(startRowIndex, rowIndex - 1, cellIndex, cellIndex));
                        cellIndex++;
                    }
                }
            }
        }
    }

}
