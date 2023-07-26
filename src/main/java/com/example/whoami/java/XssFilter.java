package com.example.whoami.java;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@WebFilter(urlPatterns = "/*") // 모든 요청에 대해 필터를 적용
public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // XSS 공격에 대비하여 입력 데이터를 이스케이프(escape) 처리
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(request);

        // 필터 체인 계속 실행
        filterChain.doFilter(wrappedRequest, response);
    }

}

