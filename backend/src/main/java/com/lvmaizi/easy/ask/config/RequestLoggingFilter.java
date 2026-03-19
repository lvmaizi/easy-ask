package com.lvmaizi.easy.ask.config;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter(urlPatterns = "/*", asyncSupported = true)
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest originHttpRequest = (HttpServletRequest) request;
        HttpServletResponse originHttpResponse = (HttpServletResponse) response;

        String requestURI = originHttpRequest.getRequestURI();
        log.info("request uri：{}", requestURI);

        chain.doFilter(originHttpRequest, originHttpResponse);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}