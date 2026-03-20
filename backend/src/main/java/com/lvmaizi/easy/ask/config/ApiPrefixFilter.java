package com.lvmaizi.easy.ask.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter(urlPatterns = "/api/*", asyncSupported = true)
@Slf4j
public class ApiPrefixFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        log.debug("API request URI: {}", requestURI);

        // 去掉 /api 前缀
        String newURI = requestURI.replaceFirst("/api", "");

        // 如果替换后为空或者没有以/开头，设置为根路径
        if (newURI.isEmpty()) {
            newURI = "/";
        }

        log.debug("Forwarding from {} to {}", requestURI, newURI);

        // 使用 RequestDispatcher 进行转发
        request.getRequestDispatcher(newURI).forward(httpRequest, httpResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("API Prefix Filter initialized");
    }

    @Override
    public void destroy() {
        log.info("API Prefix Filter destroyed");
    }
}
