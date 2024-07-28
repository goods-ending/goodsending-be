package com.goodsending.product.repository;

import com.goodsending.product.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findAllByNameContainingOrderByCreatedDateTimeDesc(String keyword);

  List<Product> findAllByOrderByCreatedDateTimeDesc();
}
