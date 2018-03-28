package liang.mvc.commons;

/**
 * Created by mc-050 on 2016/6/22.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 *
 * @author
 */
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ServletContext context;

    @Autowired
    private HttpSession httpSession;

    private static final Map<String, Object> threadLocalMap = new HashMap<>();

    private static final String REQUEST_KEY = "request";
    private static final String RESPONSE_KEY = "response";
    private static final String CONTEXT_KEY = "context";
    private static final String SESSION_KEY = "session";

    @PostConstruct
    private void init() {
        threadLocalMap.put(REQUEST_KEY, request);
        threadLocalMap.put(RESPONSE_KEY, response);
        threadLocalMap.put(CONTEXT_KEY, context);
        threadLocalMap.put(SESSION_KEY, httpSession);
    }

    //实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }


    //取得存储在静态变量中的ApplicationContext.
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
    //从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
    //如果有多个Bean符合Class, 取出第一个.
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        @SuppressWarnings("rawtypes")
        Map beanMaps = applicationContext.getBeansOfType(clazz);
        if (beanMaps != null && !beanMaps.isEmpty()) {
            return (T) beanMaps.values().iterator().next();
        } else {
            return null;
        }
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = (HttpServletRequest) threadLocalMap.get(REQUEST_KEY);
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest没有注入成功");
        }
        return request;
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = (HttpServletResponse) threadLocalMap.get(RESPONSE_KEY);
        if (response == null) {
            throw new IllegalStateException("HttpServletResponse没有注入成功");
        }
        return response;
    }

    public static ServletContext getContext() {
        ServletContext context = (ServletContext) threadLocalMap.get(CONTEXT_KEY);
        if (context == null) {
            throw new IllegalStateException("ServletContext没有注入成功");
        }
        return context;
    }

    public static HttpSession getHttpSession() {
        HttpSession httpSession = (HttpSession) threadLocalMap.get(SESSION_KEY);
        if (httpSession == null) {
            throw new IllegalStateException("ServletContext没有注入成功");
        }
        return httpSession;
    }
}
