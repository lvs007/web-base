package com.liang.mvc.service;

import com.liang.common.MyTools;
import com.liang.common.file.FileFactory;
import com.liang.common.file.FileHandler;
import com.liang.common.file.FileHandler.FileType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by liangzhiyan on 2017/3/29.
 */
@Service
public class BaseDownloadService {

    private static final String TxtSplit = "\t";
    private static final String CsvSplit = ",";

    public <T> void downloadXls(OutputStream outputStream, List<T> dataList, List<String> headList) throws IllegalAccessException {
        FileType fileType = FileHandler.FileType.XLS;
        download(outputStream, dataList, headList, fileType, null);
    }

    public <T> void downloadXlsx(OutputStream outputStream, List<T> dataList, List<String> headList) throws IllegalAccessException {
        FileHandler.FileType fileType = FileHandler.FileType.XLSX;
        download(outputStream, dataList, headList, fileType, null);
    }

    public <T> void downloadTxt(OutputStream outputStream, List<T> dataList, List<String> headList) throws IllegalAccessException {
        FileHandler.FileType fileType = FileHandler.FileType.TXT;
        download(outputStream, dataList, headList, fileType, TxtSplit);
    }

    public <T> void downloadCsv(OutputStream outputStream, List<T> dataList, List<String> headList) throws IllegalAccessException {
        FileHandler.FileType fileType = FileHandler.FileType.CSV;
        download(outputStream, dataList, headList, fileType, CsvSplit);
    }

    public <T> void download(OutputStream outputStream, List<T> dataList, List<String> headList, FileHandler.FileType fileType, String lineSplit) throws IllegalAccessException {
        FileFactory.getFileHandler(fileType).writeToStream(outputStream, fileType, MyTools.beanListToList(dataList), headList, lineSplit);
    }

    /**
     * 下载文件
     *
     * @param response
     * @param downloadFileName 生成的下文件名
     * @param dataList         下载的数据
     * @param headList         文件字段的标题
     * @param fileType         文件的类型
     * @param lineSplit        字段的分隔符，这个只在下载txt和csv文件用到，xls和xlsx不需要
     * @param <T>
     * @throws IOException
     * @throws IllegalAccessException
     */
    public <T> void download(HttpServletResponse response, String downloadFileName, List<T> dataList, List<String> headList, FileHandler.FileType fileType, String lineSplit) throws IOException, IllegalAccessException {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName + FileHandler.FileType.getPostfix(fileType), "utf-8"));
        download(response.getOutputStream(), dataList, headList, fileType, lineSplit);
    }
}
