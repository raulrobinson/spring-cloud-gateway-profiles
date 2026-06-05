package com.example.gateway.domain.ports.out;

import reactor.core.publisher.Mono;

public interface OAuthTokenPort {

    Mono<String> getAccessToken();
}
