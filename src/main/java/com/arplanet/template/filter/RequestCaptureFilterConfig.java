package com.arplanet.template.filter;

import com.arplanet.template.log.LogContext;
import com.arplanet.template.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class RequestCaptureFilterConfig {

    @Bean
    public RawRequestCaptureFilter rawRequestCaptureFilter(Logger logger, LogContext logContext, ObjectMapper objectMapper) {
        return new RawRequestCaptureFilter(logger, logContext, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<RawRequestCaptureFilter> filterRegistrationBean(RawRequestCaptureFilter filter) {
        FilterRegistrationBean<RawRequestCaptureFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(-104);

        return registrationBean;
    }

}
