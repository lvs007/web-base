package com.liang.mvc.handler.resolver;

import com.liang.mvc.commons.ErrorField;
import com.liang.mvc.commons.ResponseData;
import com.liang.mvc.exception.ServiceException;
import com.liang.mvc.exception.ServiceKeyException;
import com.liang.mvc.monitor.ControllerMonitor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 全局异常统一处理类,将错误信息转换成合适的JSON格式返回。
 *
 * @author skyfalling
 */
public class GlobalHandlerExceptionResolver extends DefaultHandlerExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalHandlerExceptionResolver.class);

    public GlobalHandlerExceptionResolver() {
        setOrder(0);
    }

    /**
     * 处理异常，返回null则不进行处理
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpRequest,
                                         HttpServletResponse httpResponse, Object handler, Exception e) {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        WebApplicationContext context = RequestContextUtils.getWebApplicationContext(httpRequest);

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setExtractValueFromSingleKeyModel(true);
        view.addStaticAttribute("view", bindError(httpResponse, context, e, locale));
        LOG.error("",e);
        return new ModelAndView(view);
    }


    /**
     * 绑定错误信息
     *
     * @param context
     * @param e
     * @param locale
     * @return
     */
    protected ResponseData bindError(HttpServletResponse httpResponse, ApplicationContext context, Exception e, Locale locale) {
        ResponseData responseData = new ResponseData();
        Errors errors = getErrors(e);
        if (e instanceof MessageSourceResolvable) {
            responseData.setMessage(context.getMessage((MessageSourceResolvable) e, locale));
        } else if (e instanceof ServiceException) {
            responseData.setMessage(e.getMessage());
        } else if (e instanceof ServiceKeyException) {
            ServiceKeyException keyException = (ServiceKeyException) e;
            responseData.setMessage(context.getMessage(keyException.getMessage(), keyException.getParams(), locale));
        } else if (errors == null) {
            responseData.setMessage(e.getMessage());
        }
        httpResponse.setStatus(httpStatus(e, errors).value());
        responseData.setErrorId(1);
        responseData.setSuccess(false);
        responseData.setFields(renderErrors(context, errors, locale));
        ControllerMonitor.get().setOutParam(responseData);
        return responseData;
    }


    /**
     * 获取HttpStatus状态码
     *
     * @param e
     * @param errors
     * @return
     */
    private HttpStatus httpStatus(Exception e, Errors errors) {
        if (e instanceof MessageSourceResolvable
                || e instanceof ServiceException
                || e instanceof ServiceKeyException || errors != null) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 渲染错误消息
     *
     * @param context
     * @param locale
     * @return
     */
    protected List<ErrorField> renderErrors(ApplicationContext context, Errors errors, Locale locale) {
        List<ErrorField> list = new ArrayList<ErrorField>();
        if (errors != null) {
            for (ObjectError error : errors.getAllErrors()) {
                String fieldName = error.getObjectName();
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    if (StringUtils.isNotEmpty(fieldError.getField())) {
                        fieldName = ((FieldError) error).getField();
                    }
                }
                list.add(new ErrorField(fieldName, context.getMessage(error, locale)));
            }
        }

        return list;
    }

    /**
     * 从异常中获取错误消息
     *
     * @param e
     * @return
     */
    private Errors getErrors(Exception e) {
        Method method = ReflectionUtils.findMethod(e.getClass(), "getBindingResult");
        if (method != null) {
            ReflectionUtils.makeAccessible(method);
            return (Errors) ReflectionUtils.invokeMethod(method, e);
        }
        return null;
    }
}
