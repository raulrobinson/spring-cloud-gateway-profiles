package com.example.gateway.domain.model;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;

import java.net.URI;

public record ProxyRequest(
        HttpMethod method,
        URI targetUri,
        HttpHeaders headers,
        Flux<DataBuffer> body
) {
}
