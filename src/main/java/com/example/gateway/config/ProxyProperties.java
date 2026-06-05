package com.example.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "proxy")
public record ProxyProperties(
        String endpointQueryParam
) {
}
