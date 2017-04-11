package liang.common.file;

import liang.common.util.ReflectUtils;
import liang.common.valid.ParameterValidate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/13 11:07.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class TxtCsvFile implements BaseFile {

    private static final Logger LOG = LoggerFactory.getLogger(TxtCsvFile.class);

    @Override
    public <T> List<T> readFile(File file, String lineSplit, Class clazz, boolean isHaveTitle) {
        List<T> result = new ArrayList<>();
        FileType fileType = FileType.isTxtCsvType(file.getName());
        try {
            List<String> lineList = FileUtils.readLines(file);
            if (isHaveTitle) {
                lineList.remove(0);
            }
            Field[] fields = clazz.getDeclaredFields();
            for (String line : lineList) {
                T t = (T) clazz.newInstance();
                String[] paramArray = StringUtils.split(line, lineSplit);
                for (int i = 0; i < paramArray.length; i++) {
                    if (StringUtils.isBlank(paramArray[i])) {
                        continue;
                    }
                    if (fileType == FileType.CSV && paramArray[i].length() > 1
                            && paramArray[i].startsWith("\"") && paramArray[i].endsWith("\"")) {
                        paramArray[i] = paramArray[i].substring(1, paramArray[i].length() - 1);
                    }
                    ReflectUtils.setValue(t, fields[i], ReflectUtils.transferParamType(paramArray[i], fields[i]));
                }
                result.add(t);
            }
        } catch (IOException e) {
            LOG.error("文件读取发生异常！", e);
        } catch (InstantiationException e) {
            LOG.error("反射创建实例异常", e);
        } catch (IllegalAccessException e) {
            LOG.error("反射创建实例异常", e);
        } catch (ParseException e) {
            LOG.error("值的类型转换异常！", e);
        } catch (Exception e) {
            LOG.error("程序异常！", e);
        }
        return result;
    }

    @Override
    public void writeFile(File file, List<List<Object>> dataList, String lineSplit) {
        writeFile(file, dataList, null, lineSplit);
    }

    @Override
    public void writeFile(File file, List<List<Object>> dataList, List<String> headList, String lineSplit) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertNull(lineSplit);
        ParameterValidate.assertCollectionNull(dataList);
        FileType fileType = FileType.isTxtCsvType(file.getName());
        try {
            writeToStream(new FileOutputStream(file), fileType, dataList, headList, lineSplit);
        } catch (FileNotFoundException e) {
            LOG.error("不存在这个文件！", e);
        }
    }

    @Override
    public void writeToStream(OutputStream outputStream, FileType fileType, List<List<Object>> dataList, List<String> headList, String lineSplit) {
        ParameterValidate.assertNull(outputStream);
        ParameterValidate.assertNull(fileType);
        ParameterValidate.assertNull(lineSplit);
        ParameterValidate.assertCollectionNull(dataList);
        try {
            StringBuffer sb = new StringBuffer();
            if (CollectionUtils.isNotEmpty(headList)) {
                setLineData(sb, fileType, lineSplit, headList);
            }
            for (List<Object> line : dataList) {
                setLineData(sb, fileType, lineSplit, line);
                outputStream.write(sb.toString().getBytes());
                outputStream.flush();
                sb.delete(0, sb.length());
            }
        } catch (IOException e) {
            LOG.error("文件写入发生异常！", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private <T> void setLineData(StringBuffer sb, FileType fileType, String lineSplit, List<T> line) {
        for (Object value : line) {
            if (fileType == FileType.CSV) {
                sb.append("\"");
                sb.append(value);
                sb.append("\"");
            } else {
                sb.append(value);
            }
            sb.append(lineSplit);
        }
        sb.delete(sb.length() - lineSplit.length(), sb.length());
        sb.append("\r\n");
    }
}
