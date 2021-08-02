package com.warehouse.service.impl;

import com.warehouse.exception.custom.ProductNotAvailableException;
import com.warehouse.exception.custom.ProductNotFoundException;
import com.warehouse.model.entity.Article;
import com.warehouse.model.AvailableProduct;
import com.warehouse.model.Part;
import com.warehouse.model.entity.Product;
import com.warehouse.model.entity.ProductPart;
import com.warehouse.repository.ArticleRepository;
import com.warehouse.repository.ProductPartRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the service implementation of Product Service.
 * It contains the service layer logic for products.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductPartRepository productPartRepository;
    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public List<Product> save(List<Product> products) {
        List<String> productNames = products.stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        List<String> existingProductsByName = productRepository.findAllByNameIn(productNames)
                .stream().map(Product::getName).collect(Collectors.toList());

        List<Product> productsToSave = products.stream()
                .filter(product -> !existingProductsByName.contains(product.getName()))
                .collect(Collectors.toList());

        products = productRepository.saveAll(productsToSave);
        saveProductParts(productsToSave);
        log.info("Saved new products: {}", products);
        return products;
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> product.setParts(getPartsOfProduct(product)));
        return products;
    }

    @Override
    public List<AvailableProduct> getAvailableOnly() {
        List<Product> products = getAll();
        Map<Long, Article> articlesById = getArticlesWithStock();
        Map<String, Integer> availableProductStock = products.stream()
                .collect(Collectors.toMap(Product::getName,
                        product -> getAvailableCount(product.getParts(), articlesById)));

        return products.stream()
                .filter(product -> availableProductStock.get(product.getName()) > 0)
                .map(product -> new AvailableProduct(product, availableProductStock.get(product.getName())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public synchronized void sellProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            log.warn("Product not found to sell with id: {}!", id);
            throw new ProductNotFoundException("Product not found to sell with id: " + id + "!");
        }
        Product productToSell = optionalProduct.get();

        List<Part> partsOfProduct = getPartsOfProduct(productToSell);
        if (!areArticlesAvailable(partsOfProduct, getArticlesWithStock())) {
            log.warn("Not enough articles to sell this product! Product: {}", productToSell.getName());
            throw new ProductNotAvailableException("Not enough articles to sell this product! Product: " + productToSell.getName());
        }

        partsOfProduct.forEach(part -> articleRepository.decreaseStock(part.getId(), part.getCount()));
        log.info("Sold product: {} and updated inventory accordingly.", productToSell.getName());
    }

    private boolean areArticlesAvailable(List<Part> parts, Map<Long, Article> articlesWithStock) {
        for (Part part : parts) {
            if (!articlesWithStock.containsKey(part.getId())
                    || articlesWithStock.get(part.getId()).getStock() < part.getCount()) {
                return false;
            }
        }
        return true;
    }

    private List<Part> getPartsOfProduct(Product product) {
        List<ProductPart> productParts = productPartRepository.findAllByProductId(product.getId());
        return productParts.stream()
                .map(productPart -> new Part(productPart.getArticleId(), productPart.getCount()))
                .collect(Collectors.toList());
    }

    private void saveProductParts(List<Product> products) {
        List<ProductPart> productParts = new ArrayList<>();
        products.forEach(product -> product.getParts()
                .forEach(part -> productParts.add(new ProductPart(product.getId(),
                                articleRepository.getById(part.getId()).getId(), part.getCount()))));
        productPartRepository.saveAll(productParts);
    }

    private Map<Long, Article> getArticlesWithStock() {
        List<Article> articlesWithStock = articleRepository.findAll();
        return articlesWithStock.stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));
    }


    private int getAvailableCount(List<Part> partsOfProduct, Map<Long, Article> articlesById) {
        int count = Integer.MAX_VALUE;

        for (Part partOfProduct : partsOfProduct) {
            if (articlesById.get(partOfProduct.getId()) == null) { // No articles found
                return 0;
            }
            int availableArticleStock = articlesById.get(partOfProduct.getId()).getStock();
            if (availableArticleStock / partOfProduct.getCount() < count) {
                count = availableArticleStock / partOfProduct.getCount();
            }
        }

        return count;
    }
}
