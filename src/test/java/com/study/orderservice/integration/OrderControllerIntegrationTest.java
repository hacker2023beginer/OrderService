package com.study.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.JsonPath;
import com.study.orderservice.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WireMockConfig.class)
@Testcontainers
@SpringBootTest
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

        MvcResult result = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Long id = JsonPath.read(json, "$.id");
        String email = JsonPath.read(json, "$.user.email");

        assertThat(id).isNotNull();
        assertThat(email).isEqualTo("test@mail.com");
    }

    @Test
    void testDockerConnection() {
        DockerClientFactory.instance().client();
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

        Long id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform((RequestBuilder) get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.user.email").value("test@mail.com"));
    }
}

