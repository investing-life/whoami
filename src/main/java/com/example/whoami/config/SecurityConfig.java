package com.example.whoami.config;

import com.example.whoami.oauth2.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 메인, 로그인, 회원가입, 비밀번호찾기 페이지는 모두 허용
        http.authorizeHttpRequests()
                .requestMatchers("/", "/login", "/login/**", "/oauth2/**", "/signup", "/find-id", "reset-pwd", "/checkDuplicateID", "/checkDuplicateEmail", "/css/**", "/js/**", "/img/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        http.oauth2Login()
                .authorizationEndpoint().baseUri("/oauth2/authorize")
                .and()
                .redirectionEndpoint().baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint().userService(customOAuth2UserService) // 사용자 정보를 가져오는 서비스 설정
                .and()
                .loginPage("/login")
                .defaultSuccessUrl("/home"); // 로그인 성공 후 리다이렉트될 경로 설정

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("id")
                .passwordParameter("password")
//                .successHandler(customAuthenticationSuccessHandler)
                .defaultSuccessUrl("/home")
                .permitAll();

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true) // 세션 무효화 여부 설정
                .deleteCookies("JSESSIONID") // 삭제할 쿠키 설정
                .permitAll();

        return http.build();

        }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return null;
            }
        };
    }


}
