package com.warehouse.service.impl;

import com.warehouse.model.entity.Article;
import com.warehouse.repository.ArticleRepository;
import com.warehouse.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * This is the service implementation of Articles Service.
 * It contains the service layer logic for articles.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional
    @Override
    public List<Article> add(List<Article> articles) {
        articles.forEach(this::saveOrUpdateArticle);
        return articles;
    }

    @Override
    public List<Article> getAll() {
        return articleRepository.findAll();
    }

    private void saveOrUpdateArticle(Article article) {
        Optional<Article> optionalArticle = articleRepository.findById(article.getId());

        if (optionalArticle.isPresent()) { // Add new stock to existing article
            Article existingArticle = optionalArticle.get();
            articleRepository.increaseStock(existingArticle.getId(), article.getStock());
            log.info("Updated stock for article: {}, from: {} to {}.", existingArticle.getId(), existingArticle.getStock(), existingArticle.getStock() + article.getStock());
        } else { // Create a new article
            Article savedArticle = articleRepository.save(article);
            log.info("Created new article: {}", savedArticle);
        }
    }
}
