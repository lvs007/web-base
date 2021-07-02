package com.day.start;

import com.liang.mvc.handler.mapping.SpringMvcConfiguration;
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
}
