package com.liang.common.file;

import com.liang.common.exception.TypeErrorException;
import com.liang.common.valid.ParameterValidate;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by mc-050 on 2016/4/27.
 */
public class ExcelUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 获取excel表的句柄，整个excel表
     */
    public static Workbook getWorkbook(File excelFile) {
        // 待完善读写excel的方法
        ParameterValidate.assertNull(excelFile);
        Workbook wb = null;
        String fileName = excelFile.getName();
        try {
            if (fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook(new FileInputStream(excelFile));
            } else if (fileName.endsWith(".xlsx")) {
                wb = new XSSFWorkbook(new FileInputStream(excelFile));
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            LOG.error("没有找到对应的文件！", e);
            return null;
        } catch (IOException e) {
            LOG.error("文件读取异常！", e);
            return null;
        }
        return wb;
    }

    /**
     * 获取第一个工作区间
     */
    public static Sheet getFirstSheet(Workbook wb) {
        if (wb != null) {
            return wb.getSheetAt(0);
        }
        return null;
    }

    /**
     * 创建HSSFSheet工作簿
     */
    public static HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
        HSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth(12);
        sheet.setGridsPrinted(false);
        sheet.setDisplayGridlines(false);
        return sheet;
    }

    /**
     * 将HSSFWorkbook写入Excel文件
     */
    public static void writeWorkbook(HSSFWorkbook wb, OutputStream outputStream) {
        try {
            wb.write(outputStream);
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    /**
     * 表头
     *
     * @param sheet
     * @param size
     */
    public static void addHeadRow(HSSFSheet sheet, int[] size, String... titleName) {
        sheet.setDefaultRowHeightInPoints(25);
        for (int i = 0; i < titleName.length; i++) {
            sheet.setColumnWidth(i, 256 * size[i]);
        }
        HSSFRow titleRow = sheet.createRow(sheet.getLastRowNum());
        int index = 0;
        for (String title : titleName) {
            titleRow.createCell(index).setCellValue(title);
        }
    }

    public static Workbook createWorkbook(File excelFile) {
        ParameterValidate.assertNull(excelFile);
        return createWorkbook(excelFile.getName());
    }

    public static Workbook createWorkbook() {
        return createWorkbook("xxx.xls");
    }

    public static Workbook createWorkbook(FileHandler.FileType fileType) {
        String fileName = "";
        if (fileType == FileHandler.FileType.XLS) {
            fileName = "xxx.xls";
        } else {
            fileName = "xxx.xlsx";
        }
        return createWorkbook(fileName);
    }

    public static Workbook createWorkbook(String fileName) {
        ParameterValidate.assertBlank(fileName);
        Workbook wb = null;
        try {
            if (fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook();
            } else if (fileName.endsWith(".xlsx")) {
                wb = new XSSFWorkbook();
            } else {
                TypeErrorException.throwException("不支持这种类型：" + fileName);
            }
        } catch (Exception e) {
            LOG.error("workbook 创建出错！", e);
            return null;
        }
        return wb;
    }

}
