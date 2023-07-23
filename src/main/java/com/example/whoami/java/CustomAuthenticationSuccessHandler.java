package com.example.whoami.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        // 이전 요청 정보 가져오기
//        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
//        if (savedRequest != null) {
//            // 이전 요청이 존재하는 경우 해당 페이지로 이동
//            String targetUrl = savedRequest.getRedirectUrl();
//            System.out.println(targetUrl);
//            getRedirectStrategy().sendRedirect(request, response, targetUrl);
//        } else {
//            // 이전 요청이 없는 경우 기본 로그인 성공 후 페이지로 이동
//            System.out.println("기본: " + request);
//            super.onAuthenticationSuccess(request, response, authentication);
//        }
//    }
//
//}
