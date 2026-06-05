package com.example.gateway.proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProxyRouter {

    @Bean
    public RouterFunction<ServerResponse> proxyRoutes(ProxyHandler handler) {
        return route()
                .nest(path("/proxy"), builder -> builder
                        .GET(handler::proxy)
                        .POST(handler::proxy)
                        .PUT(handler::proxy)
                        .PATCH(handler::proxy)
                        .DELETE(handler::proxy)
                )
                .build();
    }
}
