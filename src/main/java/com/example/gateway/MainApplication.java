package com.example.gateway;

import com.example.gateway.config.OAuthProperties;
import com.example.gateway.config.ProxyProperties;
import com.example.gateway.config.ProxySslProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        OAuthProperties.class,
        ProxyProperties.class,
        ProxySslProperties.class
})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
