package com.warehouse.exception.custom;

/**
 * This is a custom exception which is thrown when product not found
 * 7/29/21
 *
 * @author ahmetcetin
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
