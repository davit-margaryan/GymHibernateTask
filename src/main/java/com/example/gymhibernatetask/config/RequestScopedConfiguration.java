package com.example.gymhibernatetask.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class RequestScopedConfiguration {
    @Bean
    @RequestScope
    public CustomHttpRequest customHttpRequest(HttpServletRequest request) {
        return new CustomHttpRequest(request.getHeader("Authorization"));
    }
}

