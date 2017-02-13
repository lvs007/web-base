package liang.mvc.commons.file;

import liang.mvc.commons.ReflectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
    public <T> List<T> readFile(File file, String lineSplit, Class clazz) {
        List<T> result = new ArrayList<>();
        try {
            FileType fileType = FileType.isTxtCsvType(file.getName());
            List<String> lineList = FileUtils.readLines(file);
            Field[] fields = clazz.getDeclaredFields();
            for (String line : lineList) {
                T t = (T) clazz.newInstance();
                String[] paramArray = StringUtils.split(line, lineSplit);
                for (int i = 0; i < paramArray.length; i++) {
                    if (StringUtils.isBlank(paramArray[i])) {
                        continue;
                    }
                    if (fileType == FileType.CSV && paramArray[i].startsWith("\"") && paramArray[i].endsWith("\"")) {
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
        }
        return result;
    }
}
