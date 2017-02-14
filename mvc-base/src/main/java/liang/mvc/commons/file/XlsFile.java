package liang.mvc.commons.file;

import liang.mvc.commons.ReflectUtils;
import liang.mvc.commons.valid.ParameterValidate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/14 11:30.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class XlsFile implements BaseFile {

    private static final Logger LOG = LoggerFactory.getLogger(XlsFile.class);

    @Override
    public <T> List<T> readFile(File file, String lineSplit, Class clazz) {
        Workbook workbook = ExcelUtil.getWorkbook(file);
        Sheet sheet = ExcelUtil.getFirstSheet(workbook);
        List<T> result = new ArrayList<>();
        FileType.isXlsType(file.getName());
        for (Row row : sheet) {
            int i = 0;
            for (Cell cell : row) {
                try {
                    Field[] fields = clazz.getDeclaredFields();
                    T t = (T) clazz.newInstance();
                    cell.setCellType(CellType.STRING);
                    String cellValue = cell.getStringCellValue();
                    if (StringUtils.isBlank(cellValue)) {
                        continue;
                    }
                    ReflectUtils.setValue(t, fields[i], ReflectUtils.transferParamType(cellValue, fields[i]));
                    result.add(t);
                } catch (Exception e) {
                    LOG.error("读取设置每个单元格的数据出错！", e);
                }
                ++i;
            }
        }
        return null;
    }

    @Override
    public void writeFile(File file, List<List<Object>> dataList, String lineSplit) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertCollectionNull(dataList);
        Workbook workbook = ExcelUtil.createWorkbook(file);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet(file.getName());
            int i = 0;
            for (List<Object> line : dataList) {
                Row row = sheet.createRow(sheet.getLastRowNum() + i);
                int j = 0;
                for (Object value : line) {
                    Cell cell = row.createCell(row.getLastCellNum() + j);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue(value == null ? "" : value.toString());
                    ++j;
                }
                ++i;
            }
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            LOG.error("文件不存在！", e);
        } catch (IOException e) {
            LOG.error("写入异常！", e);
        }
    }
}
