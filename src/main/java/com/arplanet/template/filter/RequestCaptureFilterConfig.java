package com.arplanet.template.filter;

import com.arplanet.template.log.LogContext;
import com.arplanet.template.log.Logger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class RequestCaptureFilterConfig {

    @Bean
    public RawRequestCaptureFilter rawRequestCaptureFilter(Logger logger, LogContext logContext) {
        return new RawRequestCaptureFilter(logger, logContext);
    }

    @Bean
    public FilterRegistrationBean<RawRequestCaptureFilter> rawRequestCaptureFilter(RawRequestCaptureFilter filter) {
        FilterRegistrationBean<RawRequestCaptureFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

}
