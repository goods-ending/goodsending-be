package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<ProductSummaryDto> findByFiltersAndSort(LocalDateTime now, String openProduct, String closedProduct,
      String keyword, Long cursorId, Pageable pageable) {

    BooleanBuilder builder = new BooleanBuilder();

    // 유효한 매물 검색
    if (openProduct != null && openProduct.equals("true") && closedProduct == null) {
      // 압찰을 진행하지 않았거나, 입찰 마감 시간이 남은 상품
      builder.and(
          product.maxEndDateTime.after(now)
              .and(product.dynamicEndDateTime.isNull()
                  .or(product.dynamicEndDateTime.after(now)
                  )
              )
      );
    }

    // 마감된 매물 검색
    if (closedProduct != null && closedProduct.equals("true") && openProduct == null) {
      // 입찰 마감 시간이 지난 상품
      builder.and(
          product.dynamicEndDateTime.before(now)
              .or(product.maxEndDateTime.before(now)
              )
      );
    }

    // 키워드 검색
    if (keyword != null && !keyword.isEmpty()) {
      builder.and(product.name.containsIgnoreCase(keyword));
    }

    // 커서 기반 페이징
    if (cursorId != null) {
      builder.and(product.id.gt(cursorId));
    }

    List<Product> fetch = jpaQueryFactory
        .selectFrom(product)
        .leftJoin(product.productImages, productImage).fetchJoin()
        .where(builder)
        .limit(pageable.getPageSize()+1)
        .orderBy(
            new CaseBuilder()
                .when(product.maxEndDateTime.before(now)).then(1)
                .otherwise(0).asc(),
            product.startDateTime.asc()
        )
        .fetch();

    List<ProductSummaryDto> productSummaryDtoList = new ArrayList<>();
    for (Product product : fetch) {
      ProductSummaryDto summaryDto = ProductSummaryDto.from(product);
      productSummaryDtoList.add(summaryDto);
    }

    boolean hasNext = false;
    if (productSummaryDtoList.size() > pageable.getPageSize()) {
      productSummaryDtoList.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(productSummaryDtoList, pageable, hasNext);
  }

}
