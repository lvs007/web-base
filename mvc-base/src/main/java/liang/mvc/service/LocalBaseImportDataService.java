package liang.mvc.service;

import liang.mvc.commons.UploadUtils;
import liang.mvc.commons.exception.TypeErrorException;
import liang.mvc.commons.exception.UnExistException;
import liang.mvc.commons.file.BaseFile;
import liang.mvc.commons.file.FileFactory;
import liang.mvc.commons.file.TxtCsvFile;
import liang.mvc.commons.file.XlsFile;
import liang.mvc.dto.UploadFileInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/14 10:14.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public abstract class LocalBaseImportDataService {

    /**
     * 从文件获取上传的内容，转换成自定义的dto，转换的格式是：dto中的每一个字段顺序必须对应文件中的每一列
     *
     * @param uuid      文件上传后存在内容中，对应的uuid
     * @param clazz     需要转换成的dto类
     * @param lineSplit 每一行的分隔符
     * @param <T>
     * @return 转换后的dto列表，通过这个返回值，再转换成对应的数据库存储对象（bo）,有为空的可能注意处理
     */
    private <T> List<T> getObjectDtoList(String uuid, Class<T> clazz, String lineSplit) {
        UploadFileInfo uploadFileInfo = UploadUtils.get(uuid);
        if (uploadFileInfo == null) {
            throw UnExistException.throwException("不存在这个文件");
        }
        File uploadFile = new File(uploadFileInfo.getPath());
        BaseFile.FileType fileType;
        if (StringUtils.endsWithIgnoreCase(uploadFileInfo.getName(), ".txt")
                || StringUtils.endsWithIgnoreCase(uploadFileInfo.getName(), ".csv")) {
            fileType = BaseFile.FileType.TXT;
        } else if (StringUtils.endsWithIgnoreCase(uploadFileInfo.getName(), ".xls")
                || StringUtils.endsWithIgnoreCase(uploadFileInfo.getName(), ".xlsx")) {
            fileType = BaseFile.FileType.XLS;
        } else {
            throw TypeErrorException.throwException("文件类型不正确");
        }
        return FileFactory.getFileHandler(fileType).readFile(uploadFile, lineSplit, clazz);
    }
}
