package com.liang.start;

import com.liang.tcp.TcpServer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by mc-050 on 2016/11/15.
 */
@Configuration//配置控制
@EnableAutoConfiguration
@ComponentScan//组件扫描
@ImportResource({"classpath:applicationContext-base.xml","classpath:applicationContext.xml"})
public class Start {

    @Autowired
    private TcpServer tcpServer;

    @PostConstruct
    public void init() {
        tcpServer.startAsync(10011);
    }

    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }
}
