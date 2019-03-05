package com.liang;

import liang.mvc.handler.mapping.SpringMvcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by mc-050 on 2017/1/18 16:50. KIVEN will tell you life,send email to xxx@163.com
 */
@Configuration
public class Config extends SpringMvcConfiguration {

  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
//    @Bean
//    public FilterRegistrationBean getLoginFilter() {
//        LoginFilter loginFilter = new LoginFilter();
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(loginFilter);
//        List<String> urlPatterns = new ArrayList<String>();
//        urlPatterns.add("/*");//拦截路径，可以添加多个
//        registrationBean.setUrlPatterns(urlPatterns);
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
