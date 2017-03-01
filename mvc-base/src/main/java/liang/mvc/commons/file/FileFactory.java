package liang.mvc.commons.file;

import liang.mvc.commons.exception.NullValueException;
import liang.mvc.commons.exception.TypeErrorException;

/**
 * Created by liangzhiyan on 2017/2/28.
 */
public class FileFactory {

    public static BaseFile getFileHandler(BaseFile.FileType fileType) {
        if (fileType == null) {
            throw NullValueException.throwException("fileType为空");
        }
        switch (fileType) {
            case CSV:
            case TXT:
                return new TxtCsvFile();
            case XLS:
            case XLSX:
                return new XlsFile();
        }
        throw TypeErrorException.throwException("不支持的文件类型：" + fileType);
    }
}
