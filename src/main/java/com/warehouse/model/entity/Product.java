package com.warehouse.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.warehouse.model.Part;
import com.warehouse.model.ProductBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

/**
 * This is the product entity model which contains the products properties including parts used to construct this product.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends ProductBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column(unique = true)
    String name;

    String currency;

    Double price; // Not provided in the example json file.

    @JsonProperty("contain_articles")
    @Transient
    List<Part> parts;
}
