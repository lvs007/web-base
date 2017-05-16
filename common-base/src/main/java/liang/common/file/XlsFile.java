package liang.common.file;

import liang.common.util.ReflectUtils;
import liang.common.valid.ParameterValidate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by mc-050 on 2017/2/14 11:30.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class XlsFile implements FileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(XlsFile.class);

    @Override
    public <T> List<T> readFile(File file, String lineSplit, Class clazz, boolean isHaveTitle) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertNull(clazz);
        FileType.isXlsType(file.getName());
        Workbook workbook = ExcelUtil.getWorkbook(file);
        Sheet sheet = ExcelUtil.getFirstSheet(workbook);
        List<T> result = new ArrayList<>();
        boolean first = true;
        Field[] fields = clazz.getDeclaredFields();
        for (Row row : sheet) {
            int i = 0;
            if (first && isHaveTitle) {
                first = false;
                continue;
            }
            try {
                T t = (T) clazz.newInstance();
                for (Cell cell : row) {
                    if (i >= fields.length) {
                        break;
                    }
                    try {
                        cell.setCellType(CellType.STRING);
                        String cellValue = cell.getStringCellValue();
                        if (StringUtils.isBlank(cellValue)) {
                            continue;
                        }
                        ReflectUtils.setValue(t, fields[i], ReflectUtils.transferParamType(cellValue, fields[i]));
                    } catch (Exception e) {
                        LOG.error("读取设置每个单元格的数据出错！", e);
                    }
                    ++i;
                }
                result.add(t);
            } catch (Exception e) {
                LOG.error("读取每行数据出错！", e);
            }
        }
        return result;
    }

    @Override
    public void readFile(File file, String lineSplit, int consumerLines, boolean exceptionContinue, ConsumerData consumerData) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertNull(consumerData);
        try {
            Workbook workbook = ExcelUtil.getWorkbook(file);
            Sheet sheet = ExcelUtil.getFirstSheet(workbook);
            List<Map<String, Object>> lineList = new ArrayList<>();
            List<String> titleList = new ArrayList<>();
            int offset = 0;
            int count = 0;
            for (Row row : sheet) {
                if (offset == 0) {
                    for (Cell cell : row) {
                        try {
                            cell.setCellType(CellType.STRING);
                            String cellValue = cell.getStringCellValue();
                            if (StringUtils.isNotBlank(cellValue)) {
                                titleList.add(cellValue);
                            }
                        } catch (Exception e) {
                            LOG.error("读取设置每个单元格的数据出错！", e);
                        }
                    }
                    consumerData.readTitle(new HashSet<String>(titleList));
                } else {
                    try {
                        if (count == consumerLines) {
                            consumerData.readBody(offset, lineList);
                            count = 0;
                            lineList.clear();
                        }
                        lineList.add(readBody(row, titleList));
                    } catch (Exception e) {
                        LOG.error("解析到第{}行时出错", (offset + 1), e);
                        if (!exceptionContinue) {
                            return;
                        }
                    }
                    ++count;
                }
                ++offset;
            }
            if (count != 0) {
                consumerData.readBody(offset, lineList);
                lineList.clear();
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private Map<String, Object> readBody(Row row, List<String> titleArray) {
        Map<String, Object> map = new HashedMap();
        for (int i = 0; i < titleArray.size(); i++) {
            try {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    map.put(titleArray.get(i), cell.getStringCellValue());
                } else {
                    map.put(titleArray.get(i), null);
                }
            } catch (Exception e) {
                LOG.error("读取设置每个单元格的数据出错！", e);
            }
        }
        return map;
    }

    @Override
    public void writeFile(File file, List<List<Object>> dataList, String lineSplit) {
        writeFile(file, dataList, null, lineSplit);
    }

    @Override
    public void writeFile(File file, List<List<Object>> dataList, List<String> headList, String lineSplit) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertCollectionNull(dataList);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            writeToStream(outputStream, FileType.getFileType(file.getName()), dataList, headList, lineSplit);
        } catch (FileNotFoundException e) {
            LOG.error("文件不存在！", e);
        }
    }

    @Override
    public void writeToStream(OutputStream outputStream, FileType fileType, List<List<Object>> dataList, List<String> headList, String lineSplit) {
        ParameterValidate.assertNull(outputStream);
        ParameterValidate.assertCollectionNull(dataList);
        Workbook workbook = ExcelUtil.createWorkbook(fileType);
        try {
            Sheet sheet = workbook.createSheet();
            int i = 0;
            if (CollectionUtils.isNotEmpty(headList)) {
                setRowData(sheet, headList, i++);
            }
            for (List<Object> line : dataList) {
                setRowData(sheet, line, i++);
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            LOG.error("写入异常！", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private <T> void setRowData(Sheet sheet, List<T> line, int i) {
        Row row = sheet.createRow(i);
        int j = 0;
        for (Object value : line) {
            Cell cell = row.createCell(j++);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(value == null ? "" : value.toString());
        }
    }
}
