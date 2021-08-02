package com.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.model.entity.Article;
import com.warehouse.service.ArticleService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the unit tests for {@link ArticleController}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    private static final String RESOURCE_ARTICLES = "/v1/articles";
    private static final Article ARTICLE_1 = new Article(1L, "abc", 5);
    private static final Article ARTICLE_2 = new Article(2L, "def", 10);
    private static final List<Article> ARTICLES = List.of(ARTICLE_1, ARTICLE_2);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @Test
    void get_whenNoArticles_thenGetEmptyList() throws Exception {
        when(articleService.getAll()).thenReturn(Lists.emptyList());

        mockMvc.perform(get(RESOURCE_ARTICLES))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void get_whenArticlesExist_thenGetArticles() throws Exception {
        when(articleService.getAll()).thenReturn(ARTICLES);

        mockMvc.perform(get(RESOURCE_ARTICLES))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ARTICLES)));
    }

    @Test
    void add_whenFileCorrupted_thenGetBadRequest() throws Exception {
        String content = Files.readString(Paths.get("src/test/resources/inventory.json"), StandardCharsets.UTF_8);
        content = "corruptedData" + content;

        // Since Spring sends multipart files as POST in default, we need to override it.
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(RESOURCE_ARTICLES);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file("articlesFile", content.getBytes()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenArticlesProvided_thenGetSavedArticles() throws Exception {
        when(articleService.add(any())).thenReturn(ARTICLES);

        String content = Files.readString(Paths.get("src/test/resources/inventory.json"), StandardCharsets.UTF_8);

        // Since Spring sends multipart files as POST in default, we need to override it.
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(RESOURCE_ARTICLES);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file("articlesFile", content.getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ARTICLES)));
    }
}
