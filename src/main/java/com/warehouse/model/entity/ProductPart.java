package com.warehouse.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This is an entity model which is keeping the relation between a product and articles.
 * To construct a project, multiple parts (articles) are needed, this entity keeps how many of those parts are needed.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    long productId;

    long articleId;

    Integer count;

    public ProductPart(long productId, long articleId, Integer count) {
        this.productId = productId;
        this.articleId = articleId;
        this.count = count;
    }
}
