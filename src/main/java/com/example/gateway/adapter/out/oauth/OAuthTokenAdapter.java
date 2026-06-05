package com.example.gateway.adapter.out.oauth;
import com.example.gateway.config.OAuthProperties;
import com.example.gateway.domain.ports.out.OAuthTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class OAuthTokenAdapter implements OAuthTokenPort {

    private final OAuthProperties properties;
    private final WebClient.Builder webClientBuilder;

    private final AtomicReference<CachedToken> cachedToken = new AtomicReference<>();

    @Override
    public Mono<String> getAccessToken() {
        CachedToken current = cachedToken.get();

        if (current != null && current.isValid()) {
            return Mono.just(current.accessToken());
        }

        return requestNewToken()
                .map(response -> {
                    long expiresIn = response.expiresIn() != null ? response.expiresIn() : 300;

                    CachedToken token = new CachedToken(
                            response.accessToken(),
                            Instant.now().plusSeconds(expiresIn - 30)
                    );

                    cachedToken.set(token);
                    return token.accessToken();
                });
    }

    private Mono<TokenResponse> requestNewToken() {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", properties.grantType());
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());

        if (properties.scope() != null && !properties.scope().isBlank()) {
            form.add("scope", properties.scope());
        }

        return webClientBuilder.build()
                .post()
                .uri(properties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    private record CachedToken(String accessToken, Instant expiresAt) {
        boolean isValid() {
            return accessToken != null
                    && !accessToken.isBlank()
                    && Instant.now().isBefore(expiresAt);
        }
    }
}
