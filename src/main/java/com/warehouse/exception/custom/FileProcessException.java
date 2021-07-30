package com.warehouse.exception.custom;

/**
 * This is a custom exception to throw in case of file process issues
 * 7/29/21
 *
 * @author ahmetcetin
 */
public class FileProcessException extends RuntimeException {
    public FileProcessException(String message) {
        super(message);
    }
}
