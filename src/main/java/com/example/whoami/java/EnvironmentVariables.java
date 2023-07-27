package com.example.whoami.java;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariables {

    public static Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", System.getenv("SPRING_DATASOURCE_URL"));
        properties.put("javax.persistence.jdbc.user", System.getenv("SPRING_DATASOURCE_USERNAME"));
        properties.put("javax.persistence.jdbc.password", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        return properties;
    }
}
