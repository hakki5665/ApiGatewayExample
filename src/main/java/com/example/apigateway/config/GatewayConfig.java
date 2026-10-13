package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> flowManagerRoute() {
        String targetUri = "http://flow-manager:8081";
        RestClient restClient = RestClient.builder().baseUrl(targetUri).build();

        return RouterFunctions.route()
                .GET("/api/v1/files/**", request -> {
                    try {
                        ResponseEntity<String> response = restClient.get()
                                .uri(request.uri().getPath())
                                .headers(headers -> {
                                    headers.addAll(request.headers().asHttpHeaders());
                                    String userLogin = extractUserLogin();
                                    if (userLogin != null) {
                                        headers.set("X-User-Login", userLogin);
                                    }
                                    headers.set("X-Gateway-Route", "FlowManager-Direct");
                                    headers.remove("Cookie");
                                })
                                .retrieve()
                                .toEntity(String.class);

                        String body = response.getBody();
                        return ServerResponse.status(response.getStatusCode())
                                .headers(headers -> headers.addAll(response.getHeaders()))
                                .body(body != null ? body : "");
                    } catch (HttpStatusCodeException ex) {
                        String errorBody = ex.getResponseBodyAsString();
                        return ServerResponse.status(ex.getStatusCode())
                                .headers(headers -> headers.addAll(ex.getResponseHeaders()))
                                .body(errorBody);
                    }
                })
                .GET("/api/v1/tasks/**", request -> {
                    try {
                        ResponseEntity<String> response = restClient.get()
                                .uri(request.uri().getPath())
                                .headers(headers -> {
                                    headers.addAll(request.headers().asHttpHeaders());
                                    String userLogin = extractUserLogin();
                                    if (userLogin != null) {
                                        headers.set("X-User-Login", userLogin);
                                    }
                                    headers.set("X-Gateway-Route", "FlowManager-Direct");
                                    headers.remove("Cookie");
                                })
                                .retrieve()
                                .toEntity(String.class);

                        String body = response.getBody();
                        return ServerResponse.status(response.getStatusCode())
                                .headers(headers -> headers.addAll(response.getHeaders()))
                                .body(body != null ? body : "");
                    } catch (HttpStatusCodeException ex) {
                        String errorBody = ex.getResponseBodyAsString();
                        return ServerResponse.status(ex.getStatusCode())
                                .headers(headers -> headers.addAll(ex.getResponseHeaders()))
                                .body(errorBody);
                    }
                })
                .build();
    }

    private String extractUserLogin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            return oidcUser.getPreferredUsername();
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return (String) jwtAuth.getTokenAttributes().get("preferred_username");
        }
        return authentication.getName();
    }
}