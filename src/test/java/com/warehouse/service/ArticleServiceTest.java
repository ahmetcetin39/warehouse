package com.warehouse.service;

import com.warehouse.model.entity.Article;
import com.warehouse.repository.ArticleRepository;
import com.warehouse.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * This is the unit tests for {@link ArticleService}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    private static final Article ARTICLE_1 = new Article(1L, "abc", 5);
    private static final Article ARTICLE_2 = new Article(2L, "def", 10);
    private static final List<Article> ARTICLES = List.of(ARTICLE_1, ARTICLE_2);

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    void add_whenArticlesProvided_thenSaveArticles() {
        when(articleRepository.findById(ARTICLE_1.getId())).thenReturn(Optional.of(ARTICLE_1));
        List<Article> result = articleService.add(ARTICLES);
        assertEquals(ARTICLES, result);
    }

    @Test
    void getAll_whenArticlesFound_thenGetArticles() {
        when(articleRepository.findAll()).thenReturn(ARTICLES);
        assertEquals(ARTICLES, articleService.getAll());
    }
}