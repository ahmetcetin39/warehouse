package com.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.model.Articles;
import com.warehouse.model.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the integration tests for {@link WarehouseApplication}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WarehouseApplicationTests {
    private static final String RESOURCE_ARTICLES = "/v1/articles";
    private static final String RESOURCE_PRODUCTS = "/v1/products";

    // Since Spring sends multipart files as POST in default, we need to override it.
    private static final MockMultipartHttpServletRequestBuilder REQUEST_BUILDER_ARTICLES = MockMvcRequestBuilders.multipart(RESOURCE_ARTICLES);

    static {
        REQUEST_BUILDER_ARTICLES.with(request -> {
            request.setMethod("PUT");
            return request;
        });
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getArticles_whenNoArticles_thenGetEmptyList() throws Exception {
        mockMvc.perform(get(RESOURCE_ARTICLES))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getArticles_whenArticlesSaved_thenGetArticles() throws Exception {
        String content = Files.readString(Paths.get("src/test/resources/inventory.json"), StandardCharsets.UTF_8);
        List<Article> expectedResult = objectMapper.readValue(content, Articles.class).getArticles();

        // Save articles
        mockMvc.perform(REQUEST_BUILDER_ARTICLES.file("articlesFile", content.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        // Get articles
        mockMvc.perform(get(RESOURCE_ARTICLES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResult.size())));
    }

    @Test
    void getAvailableProducts_whenInventoryIsSufficient_thenGetAvailableProducts() throws Exception {
        String articleContent = Files.readString(Paths.get("src/test/resources/inventory.json"), StandardCharsets.UTF_8);

        // Save articles
        mockMvc.perform(REQUEST_BUILDER_ARTICLES.file("articlesFile", articleContent.getBytes()))
                .andExpect(status().isOk());

        String productContent = Files.readString(Paths.get("src/test/resources/products.json"), StandardCharsets.UTF_8);

        // Save products
        mockMvc.perform(multipart(RESOURCE_PRODUCTS).file("productsFile", productContent.getBytes()))
                .andExpect(status().isOk());

        // Get available products
        mockMvc.perform(get(RESOURCE_PRODUCTS)
                .param("isAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].count", is(2)))
                .andExpect(jsonPath("$[1].count", is(1)));
    }

    @Test
    void sellProduct_whenNoAvailableArticles_thenGetNotFound() throws Exception {
        String productContent = Files.readString(Paths.get("src/test/resources/products.json"), StandardCharsets.UTF_8);

        // Save products
        mockMvc.perform(multipart(RESOURCE_PRODUCTS).file("productsFile", productContent.getBytes()))
                .andExpect(status().isOk());

        // Get available products, should be 0. No articles added.
        mockMvc.perform(get(RESOURCE_PRODUCTS)
                .param("isAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(patch(RESOURCE_PRODUCTS + "/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void sellProduct_whenProductAvailable_thenMakeASell() throws Exception {
        String articleContent = Files.readString(Paths.get("src/test/resources/inventory.json"), StandardCharsets.UTF_8);

        // Save articles
        mockMvc.perform(REQUEST_BUILDER_ARTICLES.file("articlesFile", articleContent.getBytes()))
                .andExpect(status().isOk());

        String productContent = Files.readString(Paths.get("src/test/resources/products.json"), StandardCharsets.UTF_8);

        // Save products
        mockMvc.perform(multipart(RESOURCE_PRODUCTS).file("productsFile", productContent.getBytes()))
                .andExpect(status().isOk());

        // Get available products, should be 0. No articles added.
        mockMvc.perform(get(RESOURCE_PRODUCTS)
                .param("isAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].count", is(2)))
                .andExpect(jsonPath("$[1].count", is(1)));

        mockMvc.perform(patch(RESOURCE_PRODUCTS + "/{id}", 1))
                .andExpect(status().isNoContent());

        // Get available products, should be 0. No articles added.
        mockMvc.perform(get(RESOURCE_PRODUCTS)
                .param("isAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].count", is(1)))
                .andExpect(jsonPath("$[1].count", is(1)));
    }
}
