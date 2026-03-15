package com.study.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        WireMockServer server = new WireMockServer(9561);
        return server;
    }

    @DynamicPropertySource
    static void overrideUserServiceUrl(DynamicPropertyRegistry registry) {
        registry.add("user.service.url", () -> "http://localhost:9561");
    }
}

