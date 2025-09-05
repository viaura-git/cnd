package com.fusionsoft.cnd.com.gateway.filter;

import com.fusionsoft.cnd.com.gateway.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHeaderFilter implements GlobalFilter, Ordered {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("JwtHeaderFilter filter start");
        String jwt = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.debug("Authorization Header : {}", jwt);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            log.debug("Authorization header Bearer exist");
            jwt = jwt.substring(7);
            log.debug("jwt : {}", jwt);
            if (jwtProvider.validateToken(jwt)) {
                log.debug("jwtProvider jwt validation result is true");
                String username = jwtProvider.getUserName(jwt);
                log.debug("username : {}", username);
                String roles = String.join(",", jwtProvider.getRolesFromToken(jwt));
                log.debug("roles : {}", roles);
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("x-cnd-username", username)
                        .header("x-cnd-roles", roles)
                        .build();
                log.debug("new header value inserted : x-cnd-username : {}", username);
                log.debug("new header value inserted : x-cnd-roles : {}", roles);
                log.debug("go to chain filter");
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
