package com.study.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.JsonPath;
import com.study.orderservice.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WireMockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WireMockServer wireMockServer;

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

        MvcResult result = mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer test-token")
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

        wireMockServer.stubFor(get(urlMatching("/users/validate.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        wireMockServer.stubFor(get(urlPathMatching("/users/by-email.*"))
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

        MvcResult createResult = mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = Integer.toUnsignedLong(JsonPath.read(createResult.getResponse().getContentAsString(), "$.id"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/orders/" + id)
                                .header("Authorization", "Bearer test-token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.user.email").value("test@mail.com"));
    }
}