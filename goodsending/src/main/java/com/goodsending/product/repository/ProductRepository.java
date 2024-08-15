package com.goodsending.product.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.dto.response.ProductlikeCountDto;
import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

  @Query("SELECT new com.goodsending.product.dto.response.ProductlikeCountDto" +
      "(p.id, p.name, p.price, p.startDateTime,p.maxEndDateTime,pi.url,p.likeCount) " +
      "FROM Product p JOIN ProductImage pi ON p.id = pi.product.id " +
      "WHERE p.member = :member AND pi.id = " +
      "(SELECT MIN(pi2.id) " +
      "FROM ProductImage pi2 " +
      "WHERE pi2.product.id = p.id)")
  Page<ProductlikeCountDto> findProductsWithImageUrlByMember(Member member, Pageable pageable);


  @Lock(value = LockModeType.OPTIMISTIC)
  @Query("select p from Product p where  p.id = :id")
  Optional<Product> findByIdWithOptimisticLock(Long id);

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(
      @Param("currentDateTime") LocalDateTime currentDateTime);
}
