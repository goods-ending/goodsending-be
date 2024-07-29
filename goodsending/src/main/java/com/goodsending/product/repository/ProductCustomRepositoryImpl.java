package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.entity.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<Product> findByKeywordOrAllOrderByIdDesc(String keyword,
      Pageable pageable) {
    // 상품 목록 조회
    List<Product> fetch = jpaQueryFactory
        .selectFrom(product)
        .leftJoin(product.productImages, productImage).fetchJoin()
        .where(nameContainsKeyword(keyword))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(product.id.desc())
        .fetch();

    // 총 개수 계산
    JPQLQuery<Product> count = jpaQueryFactory
        .select(product)
        .from(product)
        .where(nameContainsKeyword(keyword));

    return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
  }

  private BooleanExpression nameContainsKeyword(String keyword) {
    if (keyword == null) return null;
    return product.name.containsIgnoreCase(keyword);
  }
}
