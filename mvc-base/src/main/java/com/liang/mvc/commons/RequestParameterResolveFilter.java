package com.liang.mvc.commons;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 处理请求参数的过滤器
 *
 * @author skyfalling
 */
public class RequestParameterResolveFilter implements Filter {
	String encode;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	encode = request.getCharacterEncoding();
    	
        request = new HttpServletRequestWrapper((HttpServletRequest) request) {
        	
            @Override
            public String getParameter(String name) {
                return resolve(super.getParameter(name));
            }

            @Override
            public Map getParameterMap() {
                Map map = super.getParameterMap();
                for (Object value : map.values()) {
                    if (value instanceof String[]) {
                        resolve((String[]) value);
                    }
                }
                return map;
            }

            @Override
            public String[] getParameterValues(String name) {
                return resolve(super.getParameterValues(name));
            }

        };
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }


    private String[] resolve(String[] values) {
        if (values != null)
            for (int i = 0; i < values.length; i++) {
                values[i] = resolve(values[i]);
            }
        return values;
    }

    private String resolve(String value) {
        if (value == null)
            return value;
        value = value.trim();
        try {
            return new String(value.getBytes(encode), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
