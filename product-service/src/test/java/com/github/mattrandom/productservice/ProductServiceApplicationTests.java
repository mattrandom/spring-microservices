package com.github.mattrandom.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mattrandom.productservice.dto.ProductRequest;
import com.github.mattrandom.productservice.model.Product;
import com.github.mattrandom.productservice.repository.ProductRepository;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    // To be able to make requests from integration test to our ProductController we use MockMvc
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Approach 1 - MockMvc")
    void shouldCreateProductMvc() throws Exception {
        //given
        ProductRequest productRequest = new ProductRequest("MacBook Pro", "Apple", BigDecimal.valueOf(10000));

        //when
        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo("MacBook Pro")));

        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @DisplayName("Approach 2 - RestAssured")
    void shouldCreateProductRA() throws Exception {
        ProductRequest productRequest = new ProductRequest("MacBook Pro", "Apple", BigDecimal.valueOf(10000));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(productRequest))
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("MacBook Pro"));
    }

    @Test
    @DisplayName("Approach 1 - MockMvc")
    void shouldRetrieveProductsMvc() throws Exception {
        //given
        Product product1 = new Product("1", "MacBook Pro", "Apple-1", BigDecimal.valueOf(10000));
        Product product2 = new Product("2", "MacBook Pro", "Apple-2", BigDecimal.valueOf(10000));

        List<Product> products = List.of(product1, product2);
        productRepository.saveAll(products);

        //when
        ResultActions response = mockMvc.perform(get("/api/products"));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(products.size())))
                .andExpect(jsonPath("$.[0].name", is(products.getFirst().getName())));

        assertEquals(2, productRepository.findAll().size());
    }

    @Test
    @DisplayName("Approach 2 - RestAssured")
    void shouldRetrieveProductsRA() {
        Product product1 = new Product("1", "MacBook Pro", "Apple-1", BigDecimal.valueOf(10000));
        Product product2 = new Product("2", "MacBook Pro", "Apple-2", BigDecimal.valueOf(10000));

        List<Product> products = List.of(product1, product2);
        productRepository.saveAll(products);

        RestAssured.get("/api/products")
                .then()
                .statusCode(200)
                .body("size()", Matchers.equalTo(products.size()))
                .body("[1].name", Matchers.equalTo(products.getFirst().getName()));

        assertEquals(2, productRepository.findAll().size());
    }
}
