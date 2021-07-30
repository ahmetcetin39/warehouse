package com.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.exception.custom.FileProcessException;
import com.warehouse.model.ProductBase;
import com.warehouse.model.Products;
import com.warehouse.model.entity.Product;
import com.warehouse.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This is the controller layer of products.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<? extends ProductBase>> get(@RequestParam(required = false) boolean isAvailable) {
        return ResponseEntity.ok(isAvailable ? productService.getAvailableOnly() : productService.getAll());
    }

    @PostMapping // It is a POST, because we are creating the Product resources
    public ResponseEntity<List<Product>> save(@RequestParam("productsFile") MultipartFile productsFile) {
        try {
            String productsAsString = new String(productsFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Products products = objectMapper.readValue(productsAsString, Products.class);
            return ResponseEntity.ok(productService.save(products.getProducts()));
        } catch (IOException e) {
            log.error("Couldn't process products file: {}!", productsFile.getName(), e);
            throw new FileProcessException("Couldn't process products file: " + productsFile.getName() + "!");
        }
    }

    @PatchMapping("/{id}") // It is a PATCH, because we are updating the resource partially
    public ResponseEntity sell(@PathVariable Long id) {
        productService.sellProduct(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
