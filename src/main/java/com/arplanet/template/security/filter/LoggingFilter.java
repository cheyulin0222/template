package com.arplanet.template.security.filter;

import com.arplanet.template.log.LogContext;
import com.arplanet.template.log.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private final Logger logger;
    private final LogContext logContext;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {


        String requestId = logContext.generateId("request");
        request.setAttribute("requestId", requestId);

        if (isJsonRequest(request)) {
            request = new ContentCachingRequestWrapper(request);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {

//            if (isJsonResponse(response)) {
//                ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//                logResponse(responseWrapper);
//                responseWrapper.copyBodyToResponse();
//            } else {
//                logResponse(response);
//            }

            logRequest(request);
        }
    }

    private void logRequest(HttpServletRequest request) {

        HashMap<String, Object> rawData = new HashMap<>();
        rawData.put("method", request.getMethod());
        rawData.put("requestURL", getFullURL(request));
        rawData.put("headers", extractHeaders(request));
        rawData.put("IP", request.getRemoteAddr());

        if (isJsonRequest(request)) {
            log.info("IS JSON REQUEST");
            String requestBody = new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), StandardCharsets.UTF_8);
            rawData.put("body", maskSensitiveInfo(requestBody));
        }

        logger.info("Request", rawData);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    private boolean isJsonResponse(HttpServletResponse response) {
        String contentType = response.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    private String maskSensitiveInfo(String body) {
        try {
            JsonNode jsonNode = objectMapper.readTree(body);

            if (jsonNode instanceof ObjectNode objectNode) {
                // 遮罩密碼相關欄位
                if (objectNode.has("password")) {
                    objectNode.put("password", "******");
                }
                if (objectNode.has("newPassword")) {
                    objectNode.put("newPassword", "******");
                }
                if (objectNode.has("confirmPassword")) {
                    objectNode.put("confirmPassword", "******");
                }

                return objectNode.toString();
            }

            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            return body;
        }
    }

    private void logResponse(HttpServletResponse response) {
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("statusCode", response.getStatus());
        responseData.put("contentType", response.getContentType());
        responseData.put("headers", getResponseHeaders(response));

//        // 如果是 JSON 響應，添加 body
//        if (isJsonResponse(response)) {
//            log.info("IS JSON RESPONSE");
//            String responseBody = new String(((ContentCachingResponseWrapper) response).getContentAsByteArray(), StandardCharsets.UTF_8);
//            responseData.put("body", responseBody);
//        }

        logger.info("Response", responseData);
    }

    private String getFullURL(HttpServletRequest request) {
        return request.getRequestURL().toString() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            String value = request.getHeader(headerName);

            // 處理敏感 header
            value = switch (headerName.toLowerCase()) {
                case "cookie" ->
                        value.replaceAll("(JSESSIONID|mongo-express)=[^;]+", "$1=*****");
                case "authorization" ->
                        value.startsWith("Bearer ") ? "Bearer *****" : "*****";
                default -> value;
            };

            headers.put(headerName, value);
        });

        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }
}
