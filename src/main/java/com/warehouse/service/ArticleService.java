package com.warehouse.service;

import com.warehouse.model.entity.Article;

import java.util.List;

/**
 * This is the service interface of Articles Service which contains the methods we expose for Articles.
 * 7/29/21
 *
 * @author ahmetcetin
 */
public interface ArticleService {
    /**
     * Adds new articles to the inventory
     *
     * @param articles to save to the inventory
     * @return the added articles
     */
    List<Article> add(List<Article> articles);

    /**
     * @return all articles
     */
    List<Article> getAll();
}
