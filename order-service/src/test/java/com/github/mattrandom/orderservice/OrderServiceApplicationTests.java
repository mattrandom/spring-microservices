package com.github.mattrandom.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mattrandom.orderservice.dto.OrderRequest;
import com.github.mattrandom.orderservice.repository.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	// To be able to make requests from integration test to our ProductController we use MockMvc
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderRepository orderRepository;

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		orderRepository.deleteAll();
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	@DisplayName("Approach 1 - MockMvc")
	void shouldCreateOrderMVC() throws Exception {
		//given
		OrderRequest orderRequest = new OrderRequest("C1", BigDecimal.valueOf(100), 2);

		//when
		ResultActions response = mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderRequest)));

		//then
		response.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderNumber", Matchers.notNullValue()))
				.andExpect(jsonPath("$.skuCode", Matchers.equalTo("C1")))
				.andExpect(jsonPath("$.price", Matchers.equalTo(100)))
				.andExpect(jsonPath("$.quantity", Matchers.equalTo(2)));

		assertEquals(1, orderRepository.findAll().size());
	}

	@Test
	@DisplayName("Approach 2 - RestAssured")
	void shouldCreateOrderRA() throws Exception {
		OrderRequest orderRequest = new OrderRequest("C1", BigDecimal.valueOf(100), 2);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(objectMapper.writeValueAsString(orderRequest))
				.when()
				.post("/api/orders")
				.then()
				.statusCode(201)
				.body("orderNumber", Matchers.notNullValue())
				.body("skuCode", Matchers.equalTo("C1"))
				.body("price", Matchers.equalTo(100))
				.body("quantity", Matchers.equalTo(2));
	}


}
