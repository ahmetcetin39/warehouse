package com.warehouse.exception.custom;

/**
 * This is a custom exception which is thrown when product not available (not in stock)
 * 7/29/21
 *
 * @author ahmetcetin
 */
public class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(String message) {
        super(message);
    }
}
