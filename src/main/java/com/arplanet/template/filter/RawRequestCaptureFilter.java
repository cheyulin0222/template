package com.arplanet.template.filter;

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
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.arplanet.template.exception.ErrorType.SYSTEM;

@RequiredArgsConstructor
public class RawRequestCaptureFilter extends OncePerRequestFilter {

    private static final List<String> SENSITIVE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/api/auth/login",
            "/api/auth/register"
    );

    private final Logger logger;
    private final LogContext logContext;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        String requestId = logContext.generateId("request");

        request.setAttribute("requestId", requestId);

        final String requestURL = request.getRequestURL().toString() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        String method = request.getMethod();
        Map<String, String> headers = extractHeaders(request);

        logger.info("Receive request");

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            boolean isSensitiveEndpoint = SENSITIVE_PATHS.stream()
                    .anyMatch(requestURL::contains);

            if (isSensitiveEndpoint) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(requestBody);

                    if (jsonNode instanceof ObjectNode objectNode) {
                        if (objectNode.has("password")) {
                            objectNode.put("password", "******");
                        }
                        requestBody = objectNode.toString();
                    }
                } catch (Exception e) {
                    logger.error("Failed to mask password in request body", e, SYSTEM);
                }
            }

            HashMap<String, Object> rawData = new HashMap<>();
            rawData.put("method", method);
            rawData.put("requestURL", requestURL);
            rawData.put("headers", headers);
            rawData.put("body", requestBody);

            logger.info("Raw data", rawData);

        }
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Collections.list(request.getHeaderNames()).forEach(headerName ->
                headers.put(headerName, request.getHeader(headerName))
        );

        return headers;
    }
}
