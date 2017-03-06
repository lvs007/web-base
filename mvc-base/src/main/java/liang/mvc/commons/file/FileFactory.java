package liang.mvc.commons.file;

import liang.mvc.commons.exception.NullValueException;
import liang.mvc.commons.exception.TypeErrorException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/2/28.
 */
public class FileFactory {

    private static final Map<BaseFile.FileType, BaseFile> fileMap = new HashMap<>();

    static {
        fileMap.put(BaseFile.FileType.TXT, new TxtCsvFile());
        fileMap.put(BaseFile.FileType.CSV, new TxtCsvFile());
        fileMap.put(BaseFile.FileType.XLS, new XlsFile());
        fileMap.put(BaseFile.FileType.XLSX, new XlsFile());
    }

    public static BaseFile getFileHandler(BaseFile.FileType fileType) {
        if (fileType == null) {
            throw NullValueException.throwException("fileType为空");
        }
        BaseFile baseFile = fileMap.get(fileType);
        if (baseFile == null) {
            throw TypeErrorException.throwException("不支持的文件类型：" + fileType);
        }
        return baseFile;
    }
}
