package com.example.whoami.java;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariables {

    public static Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
//        properties.put("jakarta.persistence.jdbc.url", System.getenv("SPRING_DATASOURCE_URL"));
//        properties.put("jakarta.persistence.jdbc.user", System.getenv("SPRING_DATASOURCE_USERNAME"));
//        properties.put("jakarta.persistence.jdbc.password", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        properties.put("hibernate.hikari.dataSource.url", System.getenv("SPRING_DATASOURCE_URL"));
        properties.put("hibernate.hikari.dataSource.user", System.getenv("SPRING_DATASOURCE_USERNAME"));
        properties.put("hibernate.hikari.dataSource.password", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        return properties;
    }
}
