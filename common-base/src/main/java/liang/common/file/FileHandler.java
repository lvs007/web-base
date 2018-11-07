package liang.common.file;

import liang.common.exception.TypeErrorException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mc-050 on 2017/2/13 11:05.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public interface FileHandler {

    String POINT = ".";

    enum FileType {
        TXT, CSV, XLS, XLSX;

        public static FileType isTxtCsvType(String name) {
            if (StringUtils.endsWithIgnoreCase(name, TXT_POSTFIX)
                    || !StringUtils.contains(name, POINT)) {
                return TXT;
            } else if (StringUtils.endsWithIgnoreCase(name, CSV_POSTFIX)) {
                return CSV;
            } else {
                throw TypeErrorException.throwException("文件类型错误！");
            }
        }

        public static FileType isXlsType(String name) {
            if (StringUtils.endsWithIgnoreCase(name, FileType.XLS.toString())) {
                return XLS;
            } else if (StringUtils.endsWithIgnoreCase(name, FileType.XLSX.toString())) {
                return XLSX;
            } else {
                throw TypeErrorException.throwException("文件类型错误！");
            }
        }

        public static FileType getFileType(String fileName) {
            if (StringUtils.endsWithIgnoreCase(fileName, TXT_POSTFIX)) {
                return FileType.TXT;
            } else if (StringUtils.endsWithIgnoreCase(fileName, CSV_POSTFIX)) {
                return FileType.CSV;
            } else if (StringUtils.endsWithIgnoreCase(fileName, XLS_POSTFIX)) {
                return FileType.XLS;
            } else if (StringUtils.endsWithIgnoreCase(fileName, XLSX_POSTFIX)) {
                return FileType.XLSX;
            } else if (!StringUtils.contains(fileName, POINT)) {
                return FileType.TXT;
            } else {
                throw TypeErrorException.throwException("文件类型不正确");
            }
        }

        public static String getPostfix(FileType fileType) {
            switch (fileType) {
                case XLSX:
                    return XLSX_POSTFIX;
                case XLS:
                    return XLS_POSTFIX;
                case TXT:
                    return TXT_POSTFIX;
                case CSV:
                    return CSV_POSTFIX;
            }
            return null;
        }

        private static final String TXT_POSTFIX = ".txt";
        private static final String CSV_POSTFIX = ".csv";
        private static final String XLS_POSTFIX = ".xls";
        private static final String XLSX_POSTFIX = ".xlsx";
    }

    public static abstract class ConsumerData {

        public abstract void readTitle(Set<String> title);

        public abstract void readBody(int offset, List<Map<String, Object>> lines);

    }

    <T> List<T> readFile(File file, String lineSplit, Class clazz, boolean isHaveTitle);

    /**
     * 边解析边消费，文件必须包含title，title为字段名
     *
     * @param file
     * @param lineSplit         每一行数据的分隔符
     * @param consumerLines     每次解析的行数
     * @param exceptionContinue 当解析过程中发生异常是否继续
     * @param consumerData      消费数据的消费者
     */
    void readFile(File file, String lineSplit, int consumerLines, boolean exceptionContinue, ConsumerData consumerData);

    void writeFile(File file, List<List<Object>> dataList, String lineSplit);

    void writeFile(File file, List<List<Object>> dataList, List<String> headList, String lineSplit);

    void writeToStream(OutputStream outputStream, FileType fileType, List<List<Object>> dataList, List<String> headList, String lineSplit);
}
