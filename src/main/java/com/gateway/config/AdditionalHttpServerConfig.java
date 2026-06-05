package com.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@Configuration
public class AdditionalHttpServerConfig {

    @Bean
    @ConditionalOnProperty(prefix = "gateway.http", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SmartLifecycle additionalHttpServer(
            ApplicationContext applicationContext,
            @Value("${gateway.http.port:8085}") int httpPort,
            @Value("${server.port}") int httpsPort,
            @Value("${server.ssl.enabled:false}") boolean sslEnabled
    ) {
        return new SmartLifecycle() {
            private volatile boolean running;
            private DisposableServer httpServer;

            @Override
            public void start() {
                if (sslEnabled && httpPort == httpsPort) {
                    throw new IllegalStateException("gateway.http.port must be different from server.port when SSL is enabled");
                }

                HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
                ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

                this.httpServer = HttpServer.create()
                        .port(httpPort)
                        .handle(adapter)
                        .bindNow();
                this.running = true;
            }

            @Override
            public void stop() {
                if (this.httpServer != null) {
                    this.httpServer.disposeNow();
                }
                this.running = false;
            }

            @Override
            public void stop(Runnable callback) {
                stop();
                callback.run();
            }

            @Override
            public boolean isRunning() {
                return this.running;
            }

            @Override
            public boolean isAutoStartup() {
                return true;
            }
        };
    }
}

