package liang.common.file;

import liang.common.MyTools;
import liang.common.util.ReflectUtils;
import liang.common.valid.ParameterValidate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
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
import java.util.Map;

/**
 * Created by mc-050 on 2017/2/13 11:07.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class TxtCsvFile implements FileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TxtCsvFile.class);

    @Override
    public <T> List<T> readFile(File file, String lineSplit, Class clazz, boolean isHaveTitle) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertNull(clazz);
        ParameterValidate.assertNull(lineSplit);
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
    public void readFile(File file, String lineSplit, int consumerLines, boolean exceptionContinue, ConsumerData consumerData) {
        ParameterValidate.assertNull(file);
        ParameterValidate.assertNull(consumerData);
        ParameterValidate.assertNull(lineSplit);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line = reader.readLine();
            String[] titleArray;
            if (StringUtils.isBlank(line)) {
                return;
            }
            titleArray = StringUtils.split(line, lineSplit);
            consumerData.readTitle(MyTools.arrayToSet(titleArray));
            line = reader.readLine();
            List<Map<String, Object>> lineList = new ArrayList<>();
            int offset = 0;
            int count = 0;
            while (line != null) {
                try {
                    if (count == consumerLines) {
                        consumerData.readBody(offset, lineList);
                        count = 0;
                        lineList.clear();
                    }
                    lineList.add(readBody(line, lineSplit, titleArray));
                    line = reader.readLine();
                } catch (Exception e) {
                    LOG.error("解析到第{}行时出错", (offset + 1), e);
                    if (!exceptionContinue) {
                        return;
                    }
                }
                ++count;
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

    private Map<String, Object> readBody(String line, String lineSplit, String[] titleArray) {
        String[] values = StringUtils.split(line, lineSplit);
        Map<String, Object> map = new HashedMap();
        for (int i = 0; i < titleArray.length; i++) {
            if (i < values.length) {
                map.put(titleArray[i], values[i]);
            } else {
                map.put(titleArray[i], null);
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
        ParameterValidate.assertBlank(lineSplit);
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
        ParameterValidate.assertBlank(lineSplit);
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
