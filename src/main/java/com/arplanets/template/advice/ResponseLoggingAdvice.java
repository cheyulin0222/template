package com.arplanets.template.advice;

import com.arplanets.template.log.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;

import static com.arplanets.template.log.enums.BasicActionType.GET_RESPONSE_BODY;

@ControllerAdvice
public class ResponseLoggingAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 處理條件
     * @param returnType the return type
     * @param converterType the selected converter type
     * @return
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends  HttpMessageConverter<?>>  converterType) {
        return true;
    }

    /**
     * 若 Response content_type 為 json ， Log Response Body
     * @param body the body to be written
     * @param returnType the return type of the controller method
     * @param selectedContentType the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request the current request
     * @param response the current response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        if (isJsonResponse(selectedContentType)) {
            HashMap<String, Object> responseData = new HashMap<>();

            responseData.put("response_body", body);

            Logger.info("Get response body", GET_RESPONSE_BODY, responseData);
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
