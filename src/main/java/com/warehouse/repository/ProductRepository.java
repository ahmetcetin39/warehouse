package com.warehouse.repository;

import com.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This is the repository interface of {@link Product} entity.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
