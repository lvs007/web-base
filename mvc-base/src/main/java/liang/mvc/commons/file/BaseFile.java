package liang.mvc.commons.file;

import liang.mvc.commons.exception.TypeErrorException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/13 11:05.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public interface BaseFile {
    enum FileType {
        TXT, CSV, XLS, XLSX;

        public static FileType isTxtCsvType(String name) {
            if (StringUtils.endsWithIgnoreCase(name, FileType.TXT.toString())) {
                return TXT;
            } else if (StringUtils.endsWithIgnoreCase(name, FileType.CSV.toString())) {
                return CSV;
            } else {
                throw TypeErrorException.getInstance("文件类型错误！");
            }
        }

        public static FileType isXlsType(String name) {
            if (StringUtils.endsWithIgnoreCase(name, FileType.XLS.toString())) {
                return XLS;
            } else if (StringUtils.endsWithIgnoreCase(name, FileType.XLSX.toString())) {
                return XLSX;
            } else {
                throw TypeErrorException.getInstance("文件类型错误！");
            }
        }
    }

    <T> List<T> readFile(File file, String lineSplit, Class clazz);
}
