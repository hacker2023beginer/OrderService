package com.study.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jayway.jsonpath.JsonPath;
import com.study.orderservice.dto.OrderDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WireMockConfig.class)
@SpringBootTest
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options().dynamicPort()
    );

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("user.service.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }


    @Test
    void createOrder_shouldReturnOrderWithUser() throws Exception {
        wireMockServer.stubFor(get(urlPathEqualTo("/users/validate"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        wireMockServer.stubFor(get(urlPathEqualTo("/users/by-email"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 1,
                                  "name": "John",
                                  "surname": "Doe",
                                  "birthDate": "1990-01-01",
                                  "email": "test@mail.com"
                                }
                                """)));

        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setEmail("test@mail.com");
        dto.setStatus("NEW");
        dto.setTotalPrice(100.0);

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        Number numberId = JsonPath.read(json, "$.id");
        Long id = numberId.longValue();

        String email = JsonPath.read(json, "$.user.email");

        assertThat(id).isNotNull();
        assertThat(email).isEqualTo("test@mail.com");
    }

    @Test
    void getOrder_shouldReturnOrderWithUser() throws Exception {
        wireMockServer.stubFor(get(urlPathEqualTo("/users/by-email"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 1,
                                  "name": "John",
                                  "surname": "Doe",
                                  "birthDate": "1990-01-01",
                                  "email": "test@mail.com"
                                }
                                """)));

        OrderDto dto = new OrderDto();
        dto.setUserId(1L);
        dto.setEmail("test@mail.com");
        dto.setStatus("NEW");
        dto.setTotalPrice(100.0);

        MvcResult createResult = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        Number numberId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
        Long id = numberId.longValue();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.user.email").value("test@mail.com"));
    }
}

