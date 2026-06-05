package com.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "proxy.ssl")
public record ProxySslProperties(
        boolean enabled,
        String trustStore,
        String trustStorePassword,
        String trustStoreType
) {
}
