/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liang.mvc.handler.mapping;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import liang.mvc.constants.SystemConfig.I18N;
import liang.mvc.handler.resolver.GlobalHandlerExceptionResolver;
import liang.mvc.handler.resolver.ScopeAttributeMethodProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 这里不能用@Configuration，因为使用@Configuration这个标签无法再引用的项目中进行@Bean注入
 * 如果这个类是和项目在一起，那么是可以直接使用@Configuration这个标签的，但是现在这个类已经被封装
 * 在基础包中，所以这里不能使用@Configuration
 */
@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurationSupport {

    @Resource
    private IdealRequestMappingConfiguration idealRequestMappingConfiguration;
    @Resource
    private MessageSource messageSource;

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        IdealRequestMappingHandlerMapping handlerMapping = new IdealRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        handlerMapping.setPackagePattern(idealRequestMappingConfiguration.getPackagePattern());
        handlerMapping.setPackageReplacement(idealRequestMappingConfiguration.getPackageReplacement());
        handlerMapping.setClassPattern(idealRequestMappingConfiguration.getClassPattern());
        handlerMapping.setClassReplacement(idealRequestMappingConfiguration.getClassReplacement());
        handlerMapping.setRequestMethodPatterns(idealRequestMappingConfiguration.getRequestMethodPatterns());
        handlerMapping.setNameResolver(idealRequestMappingConfiguration.getNameResolver());
        return handlerMapping;
    }


    @Override
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
        Method method = ReflectionUtils.findMethod(RequestMappingHandlerAdapter.class, "getDefaultArgumentResolvers");
        ReflectionUtils.makeAccessible(method);
        List<HandlerMethodArgumentResolver> argumentResolvers = (List<HandlerMethodArgumentResolver>) ReflectionUtils.invokeMethod(method, adapter);
        //优先调用自定义的ArgumentResolver
        argumentResolvers.add(0, new ScopeAttributeMethodProcessor());
        adapter.setArgumentResolvers(argumentResolvers);

        method = ReflectionUtils.findMethod(RequestMappingHandlerAdapter.class, "getDefaultReturnValueHandlers");
        ReflectionUtils.makeAccessible(method);
        List<HandlerMethodReturnValueHandler> returnValueHandlers = (List<HandlerMethodReturnValueHandler>) ReflectionUtils.invokeMethod(method, adapter);
        //优先调用自定义的ReturnValueHandler
        returnValueHandlers.add(0, new ScopeAttributeMethodProcessor(Collections.unmodifiableList(returnValueHandlers)));
        adapter.setReturnValueHandlers(returnValueHandlers);
        System.out.println("[requestMappingHandlerAdapter]");

        return adapter;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        addDefaultHttpMessageConverters(converters);
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> it = iterator.next();
            if (it instanceof StringHttpMessageConverter) {
                //移除默认编码ISO-8859-1
                iterator.remove();
            } else if (it instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) it;
                //JSON序列化忽略空值
                converter.getObjectMapper().configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false).setSerializationInclusion(Include.NON_NULL);
            }
        }
        //添加编码UTF-8
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF8")));
    }


    @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new GlobalHandlerExceptionResolver());
        addDefaultHandlerExceptionResolvers(exceptionResolvers);
    }


    @Bean
    public Validator mvcValidator() {
        Validator validator = super.mvcValidator();
        if (validator instanceof LocalValidatorFactoryBean) {
            ((LocalValidatorFactoryBean) validator).setValidationMessageSource(messageSource);
        }
        return validator;
    }


    @Bean(name = "messageSource")
    public static ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(I18N.MESSAGES, I18N.ERRORS);
        messageSource.setDefaultEncoding(I18N.ENCODING);
        return messageSource;
    }
}
