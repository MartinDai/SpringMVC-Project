package com.doodl6.springmvc.common.excel;

/**
 * Created by daixiaoming on 2018/9/19.
 */
public enum ExcelVersion {
    XLS(".xls"),
    XLSX(".xlsx");

    private String suffix;

    ExcelVersion(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
