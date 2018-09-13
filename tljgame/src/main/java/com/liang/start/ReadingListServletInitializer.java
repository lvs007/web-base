package com.liang.start;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by liangzhiyan on 2017/5/26.
 */
public class ReadingListServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(Start.class);
    }

}
