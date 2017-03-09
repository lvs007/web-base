package liang.common.http;

import liang.common.http.api.ApiResponse;
import liang.common.http.api.BaseApi;
import liang.common.http.api.exception.ApiException;
import liang.common.http.api.exception.HttpException;
import liang.common.http.api.exception.InternalException;

/**
 * Created by liangzhiyan on 2017/3/7.
 */
public class BaseApiTest {

    public static class HttpTest extends BaseApi{

        public void getList() throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/v1/test/page-list");
            System.out.println(response.getJsonObject());
        }

        @Override
        protected String getApiHost() {
            return "http://127.0.0.1:9090";
        }

        @Override
        protected String getSignKey() {
            return "nihaotest";
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
