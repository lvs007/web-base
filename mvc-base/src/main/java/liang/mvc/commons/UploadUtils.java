/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.mvc.commons;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import liang.mvc.dto.UploadFileInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 2014022602
 */
public class UploadUtils {

    private static final Cache<String, UploadFileInfo> fileCache = CacheBuilder.newBuilder().initialCapacity(100)
            .maximumSize(1000).expireAfterAccess(1, TimeUnit.HOURS).build();

    /**
     * 返回下载文件信息
     *
     * @param request
     * @param savePath
     * @return
     * @throws Exception
     */
    public static UploadFileInfo savePartItem(HttpServletRequest request, String savePath) throws Exception {
        List<UploadFileInfo> uploadFileInfos = savePartItems(request, savePath);
        if (uploadFileInfos.size() <= 0) {
            throw new IllegalStateException("请选择文件上传");
        }
        return uploadFileInfos.get(0);
    }

    /**
     * 获取多个包涵图片对象的集合
     *
     * @param request
     * @param savePath
     * @return
     * @throws Exception
     */
    public static boolean savePartItemFile(HttpServletRequest request, String savePath) throws Exception {
        Collection<Part> parts = request.getParts();
        if (CollectionUtils.isEmpty(parts)) {
            return false;
        }
        for (Part part : parts) {
            if (part.getName().equals("file")) {
                File file = new File(savePath);
                file.getParentFile().mkdirs();
                try (InputStream input = part.getInputStream()) {
                    try (OutputStream output = new FileOutputStream(file)) {
                        IOUtils.copy(input, output);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取多个包涵图片对象的集合
     *
     * @param request
     * @param savePath
     * @return
     * @throws Exception
     */
    public static List<UploadFileInfo> savePartItems(HttpServletRequest request, String savePath) throws Exception {
        Collection<Part> parts = request.getParts();
        if (CollectionUtils.isEmpty(parts)) {
            return new ArrayList<>();
        }
        List<UploadFileInfo> uploadFileInfos = new ArrayList<>();
        for (Part part : parts) {
            if (part.getName().equals("file")) {
                UploadFileInfo uploadFileInfo = new UploadFileInfo();
                String uuid = UUID.randomUUID().toString().replace("-", "");
                File file = new File(savePath + "/" + uuid);
                file.getParentFile().mkdirs();
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");//根据文件流生成MD5
                long count = 0;
                try (InputStream input = part.getInputStream()) {
                    try (OutputStream output = new FileOutputStream(file)) {
                        int bufferLength = 8 * 1024;
                        byte buffer[] = new byte[bufferLength];
                        int read = input.read(buffer, 0, bufferLength);
                        while (read > -1) {
                            messageDigest.update(buffer, 0, read);
                            output.write(buffer, 0, read);
                            read = input.read(buffer, 0, bufferLength);
                            count += read;
                        }
                    }
                }
                byte bytes_md5[] = messageDigest.digest();
                uploadFileInfo.setMd5(MD5Encoder.encode(bytes_md5));
                uploadFileInfo.setType(StringUtils.left(part.getContentType(), 32));
                uploadFileInfo.setPath(file.getAbsolutePath());
                uploadFileInfo.setFileSize(count);
                uploadFileInfo.setName(getFileName(part));
                uploadFileInfo.setUuid(uuid);
                uploadFileInfos.add(uploadFileInfo);
                fileCache.put(uuid, uploadFileInfo);
            }
        }
        return uploadFileInfos;
    }

    /**
     * 功能：JavaEE6 Part上传图片获得文件名
     *
     * @param part
     * @return
     */
    public static String getFileName(Part part) {
        String header = part.getHeader("Content-Disposition");
        return header.substring(header.indexOf("filename=\"") + 10, header.length() - 1);
    }

    /**
     * 删除文件路径
     *
     * @param path
     */
    public static boolean deleteFile(String path) {
        File uploadFile = new File(path);
        return FileUtils.deleteQuietly(uploadFile);
    }

    /**
     * 通过uuid获取文件
     *
     * @param uuid
     * @return
     */
    public static UploadFileInfo get(String uuid) {
        return fileCache.getIfPresent(uuid);
    }
}
