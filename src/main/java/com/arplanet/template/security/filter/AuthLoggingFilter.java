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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.arplanet.template.exception.ErrorType.SYSTEM;
import static com.arplanet.template.log.enums.BasicActionType.REQUEST_DETAILS;
import static com.arplanet.template.log.enums.BasicActionType.RESPONSE_METADATA;

@RequiredArgsConstructor
@Slf4j
public class AuthLoggingFilter extends OncePerRequestFilter {

    private final Logger logger;
    private final LogContext logContext;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestId = logContext.generateId("request");
        request.setAttribute("requestId", requestId);

        request = new ContentCachingRequestWrapper(request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            logRequest(request);
        }
    }

    private void logRequest(HttpServletRequest request) {

        HashMap<String, Object> rawData = new HashMap<>();
        rawData.put("method", request.getMethod());
        rawData.put("requestURL", getFullURL(request));
        rawData.put("headers", extractHeaders(request));
        rawData.put("IP", request.getRemoteAddr());

        try {
            String requestBody = new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            if (jsonNode instanceof ObjectNode objectNode) {
                maskSensitiveFields(objectNode);
                HashMap<String, Object> sensitiveData = new HashMap<>();
                sensitiveData.put("request_body", objectNode);
                logger.info("Request Details", REQUEST_DETAILS, sensitiveData);
            }

        } catch (Exception e) {
            logger.error("Failed to process sensitive data", REQUEST_DETAILS, e, SYSTEM);
        }

        logger.info("Request Details", REQUEST_DETAILS, rawData);
    }

    private void maskSensitiveFields(ObjectNode objectNode) {
        Arrays.asList("password", "newPassword", "confirmPassword").forEach(field -> {
            if (objectNode.has(field)) {
                objectNode.put(field, "******");
            }
        });
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

    private void logResponse(HttpServletResponse response) {
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("statusCode", response.getStatus());
        responseData.put("headers", getResponseHeaders(response));

        logger.info("Response Metadata", RESPONSE_METADATA, responseData);
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
