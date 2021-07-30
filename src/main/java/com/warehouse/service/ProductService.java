package com.warehouse.service;

import com.warehouse.model.AvailableProduct;
import com.warehouse.model.entity.Product;

import java.util.List;

/**
 * This is the service interface of Product Service which contains the methods we expose for Product.
 * 7/29/21
 *
 * @author ahmetcetin
 */
public interface ProductService {
    /**
     * Adds new products to the warehouse
     *
     * @param products including parts to save to warehouse
     * @return the added products
     */
    List<Product> save(List<Product> products);

    /**
     * @return all products
     */
    List<Product> getAll();

    /**
     * @return only the available products with existing inventory
     */
    List<AvailableProduct> getAvailableOnly();

    /**
     * Sells a product & removes the product parts sold from inventory
     * @param id of the product to sell
     */
    void sellProduct(Long id);
}
