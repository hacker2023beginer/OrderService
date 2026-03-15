package com.study.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.study.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(WireMockConfig.class)
@ActiveProfiles("test")
class OrderServiceValidateUserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private OrderService orderService;

    @Test
    void validateUser_shouldReturnTrue_whenUserServiceResponds() {
        wireMockServer.stubFor(get(urlPathEqualTo("/users/validate"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        Boolean result = orderService.validateUser(1L, "anna@example.com", "Bearer test-token");
        assertThat(result).isTrue();
    }

    @Test
    void validateUser_shouldFallback_whenUserServiceFails() {
        wireMockServer.stubFor(get(urlPathEqualTo("/users/validate"))
                .willReturn(aResponse().withStatus(500)));

        Boolean result = orderService.validateUser(1L, "test@mail.com", "Bearer test-token");

        assertThat(result).isFalse();
    }
}
