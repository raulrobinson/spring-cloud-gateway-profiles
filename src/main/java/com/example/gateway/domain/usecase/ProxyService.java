package com.example.gateway.domain.usecase;

import com.example.gateway.domain.model.ProxyRequest;
import com.example.gateway.domain.model.ProxyResponse;
import com.example.gateway.domain.ports.in.ProxyUseCase;
import com.example.gateway.domain.ports.out.HttpProxyPort;
import com.example.gateway.domain.ports.out.OAuthTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProxyService implements ProxyUseCase {

    private final OAuthTokenPort oauthTokenPort;
    private final HttpProxyPort httpProxyPort;

    @Override
    public Mono<ProxyResponse> proxy(ProxyRequest request) {
        return oauthTokenPort.getAccessToken()
                .flatMap(token -> {
                    HttpHeaders enrichedHeaders = new HttpHeaders();
                    enrichedHeaders.addAll(request.headers());
                    enrichedHeaders.setBearerAuth(token);

                    ProxyRequest enrichedRequest = new ProxyRequest(
                            request.method(),
                            request.targetUri(),
                            enrichedHeaders,
                            request.body()
                    );

                    return httpProxyPort.execute(enrichedRequest);
                });
    }
}
