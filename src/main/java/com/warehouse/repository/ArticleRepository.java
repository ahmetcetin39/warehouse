package com.warehouse.repository;

import com.warehouse.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This is the repository interface of {@link Article} entity.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Modifying
    @Query("update Article a SET a.stock = a.stock + :extraStock where a.id=:id")
    void increaseStock(long id, int extraStock);

    @Modifying
    @Query("update Article a SET a.stock = a.stock - :stockToDelete where a.id=:id")
    void decreaseStock(long id, int stockToDelete);
}
