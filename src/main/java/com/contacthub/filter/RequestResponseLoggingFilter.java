package com.contacthub.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    //
    private static final Logger log = LoggerFactory.getLogger("HTTP");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String requestBody = new String(wrappedRequest.getContentAsByteArray());
            String responseBody = new String(wrappedResponse.getContentAsByteArray());

            if (wrappedResponse.getStatus() >= 400) {
                log.error("--->  {} {}", request.getMethod(), request.getRequestURI());
                if (!requestBody.isBlank()) {
                    log.error("--->  Body      : {}", requestBody);
                }
                log.error("<---  Status    : {}", wrappedResponse.getStatus());
                log.error("<---  Time      : {}ms", duration);
                if (!responseBody.isBlank()) {
                    log.error("<---  Body      : {}", responseBody);
                }
            } else {
                log.info("--->  {} {}", request.getMethod(), request.getRequestURI());
                if (!requestBody.isBlank()) {
                    log.info("--->  Body      : {}", requestBody);
                }
                log.info("<---  Status    : {}", wrappedResponse.getStatus());
                log.info("<---  Time      : {}ms", duration);
            }

            // IMPORTANT: copy the cached response body back to the actual response
            wrappedResponse.copyBodyToResponse();
        }
        //
    }

    //
}
