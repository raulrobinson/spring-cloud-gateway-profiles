package com.example.gateway.adapter.in.web;

import com.example.gateway.config.ProxyProperties;
import com.example.gateway.domain.model.ProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ProxyRequestMapper {

    private final ProxyProperties proxyProperties;

    public Mono<ProxyRequest> toDomain(ServerRequest request) {
        String endpoint = request.queryParam(proxyProperties.endpointQueryParam())
                .orElseThrow(() -> new IllegalArgumentException("Missing endpoint query param"));

        if (endpoint.isBlank()) {
            return Mono.error(new IllegalArgumentException("Endpoint query param is empty"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addAll(request.headers().asHttpHeaders());

        return Mono.just(new ProxyRequest(
                request.method(),
                URI.create(endpoint),
                headers,
                request.exchange().getRequest().getBody()
        ));
    }
}
