package com.gateway.proxy;

import com.gateway.config.ProxyProperties;
import com.gateway.oauth.OAuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProxyHandler {

    private static final List<String> BLOCKED_HEADERS = List.of(
            HttpHeaders.HOST,
            HttpHeaders.CONTENT_LENGTH,
            HttpHeaders.AUTHORIZATION
    );

    private final OAuthTokenService tokenService;
    private final ProxyProperties proxyProperties;
    private final WebClient.Builder webClientBuilder;

    public Mono<ServerResponse> proxy(ServerRequest request) {
        String endpoint = request.queryParam(proxyProperties.endpointQueryParam())
                .orElse(null);

        if (endpoint == null || endpoint.isBlank()) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("Missing endpoint query param");
        }

        URI targetUri = URI.create(endpoint);

        return tokenService.getAccessToken()
                .flatMap(token -> webClientBuilder.build()
                        .method(request.method())
                        .uri(targetUri)
                        .headers(headers -> {
                            copyHeaders(request.headers().asHttpHeaders(), headers);
                            headers.setBearerAuth(token);
                        })
                        .cookies(cookies -> copyCookies(request, cookies))
                        .body(BodyInserters.fromDataBuffers(request.exchange().getRequest().getBody()))
                        .exchangeToMono(response ->
                                ServerResponse
                                        .status(response.statusCode())
                                        .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                                        .cookies(cookies -> response.cookies()
                                                .forEach((name, values) -> values.forEach(cookie ->
                                                        cookies.add(name, ResponseCookie.from(cookie.getName(), cookie.getValue())
                                                                .path(cookie.getPath())
                                                                .domain(cookie.getDomain())
                                                                .maxAge(cookie.getMaxAge())
                                                                .secure(cookie.isSecure())
                                                                .httpOnly(cookie.isHttpOnly())
                                                                .sameSite(cookie.getSameSite())
                                                                .build()
                                                        )
                                                ))
                                        )
                                        .body(BodyInserters.fromDataBuffers(response.bodyToFlux(org.springframework.core.io.buffer.DataBuffer.class)))
                        ));
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

    private void copyCookies(ServerRequest request,
                             org.springframework.util.MultiValueMap<String, String> targetCookies) {
        request.cookies().forEach((name, cookies) ->
                cookies.forEach(cookie -> targetCookies.add(name, cookie.getValue()))
        );
    }
}
