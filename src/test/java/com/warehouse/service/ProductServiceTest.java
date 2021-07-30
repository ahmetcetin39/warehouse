package com.warehouse.service;

import com.warehouse.exception.custom.ProductNotAvailableException;
import com.warehouse.exception.custom.ProductNotFoundException;
import com.warehouse.model.AvailableProduct;
import com.warehouse.model.Part;
import com.warehouse.model.entity.Article;
import com.warehouse.model.entity.Product;
import com.warehouse.model.entity.ProductPart;
import com.warehouse.repository.ArticleRepository;
import com.warehouse.repository.ProductPartRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This is the unit tests for {@link ProductService}
 * 7/29/21
 *
 * @author ahmetcetin
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final Product PRODUCT_1 = new Product(1L, "chair", "USD", 9.99, List.of(new Part(1L, 5), new Part(3L, 2)));
    private static final Product PRODUCT_2 = new Product(2L, "desk", "EUR", 20.0, List.of(new Part(1L, 3)));
    private static final List<Product> PRODUCTS = List.of(PRODUCT_1, PRODUCT_2);
    private static final Article ARTICLE_1 = new Article(1L, "abc", 6);
    private static final Article ARTICLE_2 = new Article(2L, "def", 10);
    private static final List<Article> ARTICLES = List.of(ARTICLE_1, ARTICLE_2);
    private static final ProductPart PRODUCT_PART_1 = new ProductPart(1L, PRODUCTS.get(0).getId(), 1L, 5);
    private static final ProductPart PRODUCT_PART_2 = new ProductPart(2L, PRODUCTS.get(1).getId(), 1L, 3);
    private static final ProductPart PRODUCT_PART_3 = new ProductPart(3L, PRODUCTS.get(0).getId(), 3L, 2);

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductPartRepository productPartRepository;

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void save_whenProductsProvided_thenSaveProducts() {
        when(productRepository.saveAll(any())).thenReturn(PRODUCTS);
        when(articleRepository.getById(any())).thenReturn(ARTICLE_1);
        List<Product> result = productService.save(PRODUCTS);

        assertEquals(PRODUCTS.size(), result.size());
    }

    @Test
    void getAll_whenProductsExist_thenReturnProducts() {
        when(productRepository.findAll()).thenReturn(PRODUCTS);
        when(productPartRepository.findAllByProductId(PRODUCTS.get(0).getId())).thenReturn(List.of(PRODUCT_PART_1));
        when(productPartRepository.findAllByProductId(PRODUCTS.get(1).getId())).thenReturn(List.of(PRODUCT_PART_2));

        assertEquals(PRODUCTS, productService.getAll());
    }

    @Test
    void getAvailableOnly_whenAvailableProductsExist_thenReturnAvailableProducts() {
        when(productRepository.findAll()).thenReturn(PRODUCTS);
        when(productPartRepository.findAllByProductId(PRODUCTS.get(0).getId())).thenReturn(List.of(PRODUCT_PART_1, PRODUCT_PART_3));
        when(productPartRepository.findAllByProductId(PRODUCTS.get(1).getId())).thenReturn(List.of(PRODUCT_PART_2));
        when(articleRepository.findAll()).thenReturn(ARTICLES);

        List<AvailableProduct> availableProducts = productService.getAvailableOnly();

        assertEquals(1, availableProducts.size());
        assertEquals(2, availableProducts.get(0).getCount());
    }

    @Test
    void sellProduct_whenProductNotFound_thenThrowProductNotFoundException() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.sellProduct(1L));
    }

    @Test
    void sellProduct_whenProductArticlesNotAvailable_thenThrowProductNotAvailableException() {
        when(productRepository.findById(any())).thenReturn(Optional.of(PRODUCT_1));
        when(productPartRepository.findAllByProductId(PRODUCTS.get(0).getId())).thenReturn(List.of(PRODUCT_PART_1, PRODUCT_PART_3));

        assertThrows(ProductNotAvailableException.class, () -> productService.sellProduct(1L));
    }

    @Test
    void sellProduct_whenProductAvailable_thenMakeASell() {
        when(productRepository.findById(any())).thenReturn(Optional.of(PRODUCT_2));
        when(productPartRepository.findAllByProductId(PRODUCT_2.getId())).thenReturn(List.of(PRODUCT_PART_2));
        when(articleRepository.findAll()).thenReturn(ARTICLES);

        productService.sellProduct(1L);

        verify(articleRepository).decreaseStock(PRODUCT_PART_2.getArticleId(), PRODUCT_2.getParts().get(0).getCount());
    }
}
