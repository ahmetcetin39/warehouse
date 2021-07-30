package com.warehouse.repository;

import com.warehouse.model.entity.ProductPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This is the repository interface of {@link ProductPart} entity.
 * 7/29/21
 *
 * @author ahmetcetin
 */
public interface ProductPartRepository extends JpaRepository<ProductPart, Long> {
    List<ProductPart> findAllByProductId(long productId);
}
