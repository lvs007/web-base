package liang.mvc.commons;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class ResponseUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtils.class);
    private static final String CHARSET = "UTF-8";

    public static void writeToResponse(HttpServletResponse response, String json) {
        if (StringUtils.isBlank(json))
            return;
        try {
            response.setContentType("application/json;charset=" + CHARSET);
            response.setContentLength(json.getBytes(CHARSET).length);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (Exception e) {
            LOG.error("Response write has error json={}", e.getMessage(), json, e);
        }
    }

    public static void writeToResponse(HttpServletResponse response, ResponseData responseData) {
        if (responseData == null) {
            return;
        }
        writeToResponse(response, JSON.toJSONString(responseData));
    }

    public static ResponseData FlowControlErrorResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(40001);
        responseData.setMessage("访问用户太多，请稍后重试");
        responseData.setSuccess(false);
        return responseData;
    }

    public static ResponseData ErrorResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(40000);
        responseData.setMessage("访问失败");
        responseData.setSuccess(false);
        return responseData;
    }

    public static ResponseData SuccessResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(200);
        responseData.setMessage("成功");
        responseData.setSuccess(true);
        return responseData;
    }
}
