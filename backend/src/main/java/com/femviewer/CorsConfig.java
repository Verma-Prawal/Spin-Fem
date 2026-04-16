package com.femviewer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * Unconditionally injects CORS headers on every HTTP response.
 *
 * WHY a raw Filter instead of WebMvcConfigurer.addCorsMappings():
 *  - Spring's CORS engine only adds Access-Control-Allow-Origin when the
 *    request carries a valid (non-null) Origin header.
 *  - Android WebViews that load pages from file:// send  Origin: null
 *    (or omit the header entirely), which Spring silently rejects.
 *  - A servlet Filter runs BEFORE Spring's CORS interceptor and injects
 *    the header unconditionally, making it work for null-origin requests too.
 */
@Configuration
public class CorsConfig {

    @Bean
    @Order(1)   // Run before any Spring security / MVC filters
    public Filter globalCorsFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest req,
                                 ServletResponse res,
                                 FilterChain chain)
                    throws IOException, ServletException {

                HttpServletRequest  request  = (HttpServletRequest)  req;
                HttpServletResponse response = (HttpServletResponse) res;

                // Allow any origin (including null / file://)
                response.setHeader("Access-Control-Allow-Origin",  "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setHeader("Access-Control-Max-Age",       "3600");

                // Handle pre-flight OPTIONS request immediately
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }

                chain.doFilter(req, res);
            }
        };
    }
}
