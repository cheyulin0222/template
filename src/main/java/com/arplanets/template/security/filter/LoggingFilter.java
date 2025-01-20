package com.arplanets.template.security.filter;

import com.arplanets.template.dto.RequestContext;
import com.arplanets.template.log.LogContext;
import com.arplanets.template.log.Logger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.arplanets.template.exception.ErrorType.SYSTEM;
import static com.arplanets.template.log.enums.BasicActionType.*;
import static com.arplanets.template.log.enums.LoggingActionType.LOG_REQUEST_RESPONSE_INFO;

/**
 * Log request 和 response 資料 ， 若請求 content_type 為 json ， 會 log request body
 */
@RequiredArgsConstructor
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private final LogContext logContext;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 產生 request_id
            String requestId = logContext.generateId("request");

            // 產生 requestContext
            RequestContext requestContext = RequestContext.builder()
                    .requestId(requestId)
                    .build();

            // 將 requestContext 存到 HttpServletRequest
            request.setAttribute("requestContext", requestContext);

            // 若 request content_type 為 json ，備份 request body
            if (isJsonRequest(request)) {
                request = new ContentCachingRequestWrapper(request);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            Logger.error("Something went wrong before logging request and response", LOG_REQUEST_RESPONSE_INFO, SYSTEM);
        } finally {
            try {
                // 紀錄 response 資料
                logResponse(response);
            } catch (Exception ex) {
                Logger.error("Failed to log response", LOG_REQUEST_RESPONSE_INFO, SYSTEM);
            }

            try {
                // 紀錄 request 資料
                logRequest(request);
            } catch (Exception ex) {
                Logger.error("Failed to log request", LOG_REQUEST_RESPONSE_INFO, SYSTEM);
            }
        }
    }

    private void logRequest(HttpServletRequest request) {

        HashMap<String, Object> rawData = new HashMap<>();
        rawData.put("method", request.getMethod());
        rawData.put("requestURL", getFullURL(request));
        rawData.put("headers", extractHeaders(request));
        rawData.put("IP", request.getRemoteAddr());

        if (isJsonRequest(request)) {
            String requestBody = new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), StandardCharsets.UTF_8);
            rawData.put("request_body", requestBody);
        }

        Logger.info("Request Details", GET_REQUEST_DETAILS, rawData);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private void logResponse(HttpServletResponse response) {
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("status_code", response.getStatus());
        responseData.put("headers", getResponseHeaders(response));

        Logger.info("Response Metadata", GET_RESPONSE_METADATA, responseData);
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
