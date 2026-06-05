package com.example.gateway.adapter.out.http;

import com.example.gateway.config.ProxySslProperties;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        ProxySslProperties.class
})
public class WebClientConfig {

    private final ProxySslProperties sslProperties;
    private final ResourceLoader resourceLoader;

    @Bean
    public WebClient.Builder webClientBuilder() {
        if (!sslProperties.enabled()) {
            return WebClient.builder();
        }

        try {
            KeyStore trustStore = KeyStore.getInstance(sslProperties.trustStoreType());

            try (InputStream inputStream = resourceLoader
                    .getResource(sslProperties.trustStore())
                    .getInputStream()) {

                trustStore.load(
                        inputStream,
                        sslProperties.trustStorePassword().toCharArray()
                );
            }

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(trustStore);

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(trustManagerFactory)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(ssl -> ssl.sslContext(sslContext));

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient));

        } catch (Exception ex) {
            throw new IllegalStateException("Error loading proxy SSL truststore", ex);
        }
    }
}
