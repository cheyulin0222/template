package com.arplanet.template.advice;

import com.arplanet.template.log.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;

import static com.arplanet.template.log.enums.BasicActionType.GET_RESPONSE_BODY;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ResponseLoggingAdvice implements ResponseBodyAdvice<Object> {

    private final Logger logger;

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends  HttpMessageConverter<?>>  converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        if (isJsonResponse(selectedContentType)) {
            HashMap<String, Object> responseData = new HashMap<>();

            responseData.put("response_body", body);

            logger.info("Get response body", GET_RESPONSE_BODY, responseData);
        }

        return body;
    }

    private boolean isJsonResponse(MediaType selectedContentType) {

        if (selectedContentType == null) {
            return false;
        }

        return selectedContentType.includes(MediaType.APPLICATION_JSON);
    }

}
