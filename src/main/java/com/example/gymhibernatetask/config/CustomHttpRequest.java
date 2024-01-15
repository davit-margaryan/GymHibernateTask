package com.example.gymhibernatetask.config;

public class CustomHttpRequest {
    private final String authorizationHeader;

    public CustomHttpRequest(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }
}
