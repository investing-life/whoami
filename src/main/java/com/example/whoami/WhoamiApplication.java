package com.example.whoami;

import com.example.whoami.java.XssFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhoamiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhoamiApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
		FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new XssFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
