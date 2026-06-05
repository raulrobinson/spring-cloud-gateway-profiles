package com.example.gateway.domain.model;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Flux;

public record ProxyResponse(
        HttpStatusCode statusCode,
        HttpHeaders headers,
        Flux<DataBuffer> body
) {
}
