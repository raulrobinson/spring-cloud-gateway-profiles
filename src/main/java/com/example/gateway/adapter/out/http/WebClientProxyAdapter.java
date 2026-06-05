package com.example.gateway.adapter.out.http;

import com.example.gateway.domain.model.ProxyRequest;
import com.example.gateway.domain.model.ProxyResponse;
import com.example.gateway.domain.ports.out.HttpProxyPort;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class WebClientProxyAdapter implements HttpProxyPort {

    private static final List<String> BLOCKED_HEADERS = List.of(
            HttpHeaders.HOST,
            HttpHeaders.CONTENT_LENGTH
    );

    private final WebClient webClient;

    public WebClientProxyAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<ProxyResponse> execute(ProxyRequest request) {
        return webClient
                .method(request.method())
                .uri(request.targetUri())
                .headers(headers -> copyHeaders(request.headers(), headers))
                .body(BodyInserters.fromDataBuffers(request.body()))
                .exchangeToMono(response -> {
                    Flux<DataBuffer> body = response.bodyToFlux(DataBuffer.class);

                    return Mono.just(new ProxyResponse(
                            response.statusCode(),
                            response.headers().asHttpHeaders(),
                            body
                    ));
                });
    }

    private void copyHeaders(HttpHeaders source, HttpHeaders target) {
        source.forEach((name, values) -> {
            if (!isBlockedHeader(name)) {
                target.put(name, values);
            }
        });
    }

    private boolean isBlockedHeader(String name) {
        return BLOCKED_HEADERS.stream()
                .anyMatch(blocked -> blocked.equalsIgnoreCase(name));
    }
}
