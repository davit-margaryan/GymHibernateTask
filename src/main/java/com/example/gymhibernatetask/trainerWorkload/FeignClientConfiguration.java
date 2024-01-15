package com.example.gymhibernatetask.trainerWorkload;

import com.example.gymhibernatetask.config.CustomHttpRequest;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class FeignClientConfiguration {
    @Autowired
    private CustomHttpRequest customHttpRequest;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (!StringUtils.isEmpty(customHttpRequest.getAuthorizationHeader())) {
                requestTemplate.header("Authorization", customHttpRequest.getAuthorizationHeader());
            }
        };
    }
}