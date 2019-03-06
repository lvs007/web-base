package com.liang.common.file;

import com.liang.common.exception.NullValueException;
import com.liang.common.exception.TypeErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/2/28.
 */
public class FileFactory {

    private static final Map<FileHandler.FileType, FileHandler> fileMap = new HashMap<>();

    static {
        fileMap.put(FileHandler.FileType.TXT, new TxtCsvFile());
        fileMap.put(FileHandler.FileType.CSV, new TxtCsvFile());
        fileMap.put(FileHandler.FileType.XLS, new XlsFile());
        fileMap.put(FileHandler.FileType.XLSX, new XlsFile());
    }

    public static FileHandler getFileHandler(FileHandler.FileType fileType) {
        if (fileType == null) {
            throw NullValueException.throwException("fileType为空");
        }
        FileHandler fileHandler = fileMap.get(fileType);
        if (fileHandler == null) {
            throw TypeErrorException.throwException("不支持的文件类型：" + fileType);
        }
        return fileHandler;
    }

    public static FileHandler getFileHandler(File file) {
        FileHandler.FileType fileType = FileHandler.FileType.getFileType(file.getName());
        return getFileHandler(fileType);
    }
}
