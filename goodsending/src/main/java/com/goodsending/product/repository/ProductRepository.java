package com.goodsending.product.repository;

import com.goodsending.product.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Lock(value = LockModeType.OPTIMISTIC)
  @Query("select p from Product p where  p.id = :id")
  Optional<Product> findByIdWithOptimisticLock(Long id);
}
