package com.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.exception.custom.FileProcessException;
import com.warehouse.model.ProductBase;
import com.warehouse.model.Products;
import com.warehouse.model.entity.Product;
import com.warehouse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    @Operation(summary = "Get products")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Found products"))
    public ResponseEntity<List<? extends ProductBase>> get(@Parameter(description = "To get the available products only")
                                                           @RequestParam(required = false) boolean isAvailable) {
        return ResponseEntity.ok(isAvailable ? productService.getAvailableOnly() : productService.getAll());
    }

    // It is a POST, because we are creating the Product resources
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Save new products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved products"),
            @ApiResponse(responseCode = "400", description = "Couldn't process the file"),
            @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
    public ResponseEntity<List<Product>> save(@Parameter(description = "File which contains products to save")
                                              @RequestParam("productsFile") MultipartFile productsFile) {
        try {
            String productsAsString = new String(productsFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Products products = objectMapper.readValue(productsAsString, Products.class);
            return ResponseEntity.ok(productService.save(products.getProducts()));
        } catch (IOException e) {
            log.error("Couldn't process products file: {}!", productsFile.getName(), e);
            throw new FileProcessException("Couldn't process products file: " + productsFile.getName() + "!");
        }
    }

    @Operation(summary = "Sell a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sold product"),
            @ApiResponse(responseCode = "400", description = "Couldn't process the file"),
            @ApiResponse(responseCode = "404", description = "Not available product found to sell")
    })
    @PatchMapping("/{id}") // It is a PATCH, because we are updating the resource partially
    public ResponseEntity<Long> sell(@Parameter(description = "id of the product to sell")
                                     @PathVariable Long id) {
        productService.sellProduct(id);
        return ResponseEntity.ok(id);
    }
}
