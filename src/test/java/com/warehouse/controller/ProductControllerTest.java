package com.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.model.AvailableProduct;
import com.warehouse.model.Part;
import com.warehouse.model.entity.Product;
import com.warehouse.service.ProductService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the unit tests for {@link ProductController}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    private static final String RESOURCE_PRODUCTS = "/v1/products";
    private static final Product PRODUCT_1 = new Product(1L, "chair", "USD", 9.99, List.of(new Part(1L, 5)));
    private static final Product PRODUCT_2 = new Product(2L, "desk", "EUR", 20.0, List.of(new Part(1L, 3)));
    private static final List<Product> PRODUCTS = List.of(PRODUCT_1, PRODUCT_2);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void get_whenNoProducts_thenGetEmptyList() throws Exception {
        when(productService.getAll()).thenReturn(Lists.emptyList());

        mockMvc.perform(get(RESOURCE_PRODUCTS))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void get_whenProductsExist_thenGetProducts() throws Exception {
        when(productService.getAll()).thenReturn(PRODUCTS);

        mockMvc.perform(get(RESOURCE_PRODUCTS))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(PRODUCTS)));
    }

    @Test
    void get_whenIsAvailableTrue_thenGetAvailableProductsOnly() throws Exception {
        List<AvailableProduct> availableProducts = List.of(new AvailableProduct(PRODUCT_1, 2), new AvailableProduct(PRODUCT_2, 3));

        when(productService.getAvailableOnly()).thenReturn(availableProducts);

        mockMvc.perform(get(RESOURCE_PRODUCTS)
                .param("isAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(availableProducts)));
    }

    @Test
    void add_whenFileCorrupted_thenGetBadRequest() throws Exception {
        String content = Files.readString(Paths.get("src/test/resources/products.json"), StandardCharsets.UTF_8);
        content = "corruptedData" + content;

        mockMvc.perform(multipart(RESOURCE_PRODUCTS).file("productsFile", content.getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenFileNotGiven_thenUnsupportedMediaType() throws Exception {
        mockMvc.perform(post(RESOURCE_PRODUCTS))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void add_whenProductsProvided_thenGetSavedProducts() throws Exception {
        when(productService.save(any())).thenReturn(PRODUCTS);

        String content = Files.readString(Paths.get("src/test/resources/products.json"), StandardCharsets.UTF_8);

        mockMvc.perform(multipart(RESOURCE_PRODUCTS).file("productsFile", content.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(PRODUCTS)));
    }

    @Test
    void sell_whenProductSold_thenGetNoContent() throws Exception {
        mockMvc.perform(patch(RESOURCE_PRODUCTS + "/{id}", 1))
                .andExpect(status().isOk());
    }
}
