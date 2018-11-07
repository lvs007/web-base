/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.mvc.action;

import liang.cache.BaseCache;
import liang.cache.impl.DefaultCommonLocalCache;
import liang.mvc.annotation.Login;
import liang.mvc.commons.SpringContextHolder;
import liang.mvc.commons.UploadUtils;
import liang.mvc.dto.UploadFileInfo;
import liang.mvc.filter.LoginUtils;
import liang.mvc.filter.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共上传文件到本地的action，存放的目录是/data/temp
 */
@Controller
public class UploadController {

    private BaseCache<String, Object> baseCache = new DefaultCommonLocalCache(10 * 60);

    /**
     * 上传文件
     *
     * @return
     * @throws Exception
     */
    @Login
    @ResponseBody
    public Map<String, String> upload() throws Exception {
        String fileUploadTempDir = "/data/temp";
        UploadFileInfo uploadFileInfo = UploadUtils.savePartItem(SpringContextHolder.getRequest(), fileUploadTempDir);
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uploadFileInfo.getUuid());
        return data;
    }

    private void validUpload() {
        HttpServletRequest request = SpringContextHolder.getRequest();
        UserInfo userInfo = LoginUtils.getCurrentUser(request);
    }
}
