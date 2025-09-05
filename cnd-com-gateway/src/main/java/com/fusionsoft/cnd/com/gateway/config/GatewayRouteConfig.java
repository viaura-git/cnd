package com.fusionsoft.cnd.com.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**").or()
                        .path("/api/test/**")
                        .uri("http://auth-service.cnd-dev.svc.cluster.local:80"))
                .route("member-service", r -> r.path("/api/members/v1/**")
                        .uri("http://member-service.cnd-dev.svc.cluster.local:80"))
                .route("reserve-service", r -> r.path("/api/reserves/v1/**")
                        .uri("http://reserve-service.cnd-dev.svc.cluster.local:80"))
                .build();
    }
}