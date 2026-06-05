package com.example.gateway.adapter.in.web;

import com.example.gateway.domain.ports.in.ProxyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProxyHandler {

    private final ProxyUseCase proxyUseCase;
    private final ProxyRequestMapper mapper;

    public Mono<ServerResponse> proxy(ServerRequest request) {
        return mapper.toDomain(request)
                .flatMap(proxyUseCase::proxy)
                .flatMap(response -> ServerResponse
                        .status(response.statusCode())
                        .headers(headers -> headers.addAll(response.headers()))
                        .body(BodyInserters.fromDataBuffers(response.body())))
                .onErrorResume(IllegalArgumentException.class, ex ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(ex.getMessage()));
    }
}
