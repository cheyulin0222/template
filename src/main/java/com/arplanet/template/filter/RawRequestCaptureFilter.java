package com.arplanet.template.filter;

import com.arplanet.template.log.LogContext;
import com.arplanet.template.log.Logger;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RawRequestCaptureFilter extends OncePerRequestFilter {

    private final Logger logger;
    private final LogContext logContext;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        String requestId = logContext.generateId("request");

        request.setAttribute("requestId", requestId);

        String requestURL = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            requestURL += "?" + request.getQueryString();
        }

        String method = request.getMethod();
        Map<String, String> headers = extractHeaders(request);

        logger.info("Receive request");

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

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
