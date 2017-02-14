/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.mvc.action;

import liang.mvc.commons.SpringContextHolder;
import liang.mvc.commons.UploadUtils;
import liang.mvc.dto.UploadFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共上传文件到本地的action，存放的目录是/data/temp
 */
@Controller
public class UploadController {

    @Autowired
    private SpringContextHolder contextHolder;

    /**
     * 上传文件
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    public Map<String, String> upload() throws Exception {
        String fileUploadTempDir = "/data/temp";
        UploadFileInfo uploadFileInfo = UploadUtils.savePartItem(contextHolder.getRequest(), fileUploadTempDir);
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uploadFileInfo.getUuid());
        return data;
    }
}
