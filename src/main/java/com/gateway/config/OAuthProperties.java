package com.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth")
public record OAuthProperties(
        String tokenUri,
        String clientId,
        String clientSecret,
        String grantType,
        String scope
) {
}
