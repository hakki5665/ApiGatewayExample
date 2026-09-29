package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
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
}