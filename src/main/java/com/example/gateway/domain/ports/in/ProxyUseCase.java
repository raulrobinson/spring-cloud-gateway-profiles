package com.example.gateway.domain.ports.in;

import com.example.gateway.domain.model.ProxyRequest;
import com.example.gateway.domain.model.ProxyResponse;
import reactor.core.publisher.Mono;

public interface ProxyUseCase {

    Mono<ProxyResponse> proxy(ProxyRequest request);
}
