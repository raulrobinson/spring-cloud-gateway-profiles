package com.example.gateway.domain.ports.out;

import com.example.gateway.domain.model.ProxyRequest;
import com.example.gateway.domain.model.ProxyResponse;
import reactor.core.publisher.Mono;

public interface HttpProxyPort {

    Mono<ProxyResponse> execute(ProxyRequest request);
}
