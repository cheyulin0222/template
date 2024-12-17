package com.arplanet.template.advice;

import com.arplanet.template.log.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

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

            responseData.put("body", body);

            logger.info("ResponseBody", responseData);
        }

        return body;
    }

//    private void logResponse(ServerHttpResponse response, Object body) {
//        HashMap<String, Object> responseData = new HashMap<>();
//        responseData.put("headers", getResponseHeaders(response));
//
//        // 如果是 JSON 響應，添加 body
//        if (isJsonResponse(response)) {
//            responseData.put("body", body);
//        }
//
//        logger.info("Response", responseData);
//    }

    private boolean isJsonResponse(MediaType selectedContentType) {

        if (selectedContentType == null) {
            return false;
        }

        return selectedContentType.includes(MediaType.APPLICATION_JSON);
    }

    private Map<String, String> getResponseHeaders(ServerHttpResponse response) {
        Map<String, String> headers = new HashMap<>();
        HttpHeaders headers1 = response.getHeaders();
        headers1.forEach((key, value) -> {
            log.info("headersssssssssssssssssssss");
            log.info("key={}, value={}", key, value);
            String headerValue = String.join(", ", value);

            // 過濾敏感 header
            headerValue = switch (key.toLowerCase()) {
                case "set-cookie" -> headerValue.replaceAll("(?<=sessionId=)[^;]+", "*****");
                case "x-api-key", "authorization" -> "*****";
                case "location" -> headerValue.replaceAll("(?<=token=)[^&]+", "*****");
                default -> headerValue;
            };

            headers.put(key, headerValue);
        });
        return headers;
    }
}
