package com.liang.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liang.common.http.api.ApiResponse;
import com.liang.common.http.api.BaseApi;
import com.liang.common.http.api.exception.ApiException;
import com.liang.common.http.api.exception.HttpException;
import com.liang.common.http.api.exception.InternalException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class BaseApiTest {

    public static class HttpTest extends BaseApi {

        public void getList() throws InternalException, ApiException, HttpException, IOException, URISyntaxException {
            HttpClient httpClient = getHttpClient();
            httpClient.setCookie("attempt.do","JSESSIONID","B8FE03DE4D46BE6385E8D445BB73E371")
                    .setCookie("attempt.do","SID","10q8ksgel568fjbqgh924ua6l3")
                    .setCookie("attempt.do","ssoid","3bb7f8618f*844feae85774222923942")
                    .setCookie("attempt.do","ssoid.sig","PPuuNrWzOYoI-bteZbFYlrg86TM")
                    .setCookie("attempt.do","dacs.sid","5FUgEXpusJRUYt-o74isV1XlndBx3vj9")
                    .setCookie("attempt.do","dacs.sid.sig","K2W5I6z3jUDkEvsD4qr2AcNvg3Q")
                    .setCookie("attempt.do","userId","2069920")
                    .setCookie("attempt.do","u","1833463")
                    .setCookie("attempt.do","userId.sig","NWIoO0L531xO-QPcwiOj0ernO3A")
                    .setCookie("attempt.do","statDs","BANMA_MESSAGE_DB_CONNECT_URL");
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            NameValuePair nameValuePair = new NameValuePair("action","attempt");
            NameValuePair nameValuePair2 = new NameValuePair("taskID","task_201712261418_0166");
            nameValuePairList.add(nameValuePair);
            nameValuePairList.add(nameValuePair2);
            String result = httpClient.httpPost("https://crane.sankuai.com/attempt.do",nameValuePairList);
            System.out.println("result: "+result);
            JSONObject jsonObject = JSON.parseObject(result);
            ApiResponse response = new ApiResponse(jsonObject);
            System.out.println("返回的结果："+response.getJsonObject());
        }

        @Override
        protected String getApiHost() {
            return "https://crane.sankuai.com";
        }

        @Override
        protected String getSignKey() {
            return null;
        }
    }

    public static void main(String[] args) {
        HttpTest httpTest = new HttpTest();
        try {
            httpTest.getList();
//            System.out.println("nihao");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
