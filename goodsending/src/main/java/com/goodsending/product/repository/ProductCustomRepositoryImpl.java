package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    boolean open = false;
    if (openProduct != null && openProduct.equals("true")) {
      open = true;
    }

    boolean closed = false;
    if (closedProduct != null && closedProduct.equals("true")) {
      closed = true;
    }

    if (open == closed) {
      open = true;
      closed = true;
    }

    // 키워드 검색
    BooleanBuilder keywordBuilder = new BooleanBuilder();
    if (keyword != null && !keyword.isEmpty()) {
      keywordBuilder.and(product.name.containsIgnoreCase(keyword));
    }

    // 커서 기반 페이징
    BooleanBuilder cursorBuilder = new BooleanBuilder();
    if (cursorId != null) {
      cursorBuilder.and(product.id.gt(cursorId));
    }

    // 구매 가능한 상품 중 시작 시간이 가장 가까운 상품
    List<Product> firstFetch = jpaQueryFactory
        .selectFrom(product)
        .where(openBuilderExpression(now), keywordBuilder)
        .limit(1)
        .orderBy(product.startDateTime.asc())
        .fetch();
    Product firstOpenProduct = firstFetch.get(0);

    // 구매 가능한 상품 중 시작 시간이 가장 먼 상품
    List<Product> lastFetch = jpaQueryFactory
        .selectFrom(product)
        .where(openBuilderExpression(now), keywordBuilder)
        .limit(1)
        .orderBy(product.startDateTime.desc())
        .fetch();
    Product lastOpenProduct = lastFetch.get(0);

    // 구매 가능한 상품 목록
    List<Product> openFetch = new ArrayList<>();
    if (open && (cursorId == null || cursorId > firstOpenProduct.getId() && cursorId != lastOpenProduct.getId())) {
      openFetch = jpaQueryFactory
          .selectFrom(product)
          .leftJoin(product.productImages, productImage).fetchJoin()
          .where(openBuilderExpression(now), cursorBuilder.and(keywordBuilder))
          .limit(pageable.getPageSize() + 1)
          .orderBy(product.startDateTime.asc())
          .fetch();
    }

    boolean hasNext = false;
    if (openFetch.size() > pageable.getPageSize()) {
      openFetch.remove(pageable.getPageSize());
      hasNext = true;
    }

    // cursorId가 null 이거나
    // 구매 가능 목록과 마감 목록을 함께 조회해 리스트에 담아야 할 경우
    // cursorBuilder 를 초기화하여 사용하지 않음
    if (cursorId != null && open && (cursorId == lastOpenProduct.getId() || (openFetch.size() < pageable.getPageSize() && openFetch.size() > 0))) {
      cursorBuilder = new BooleanBuilder();
    }

    // 마감된 상품 목록
    List<Product> closedFetch = new ArrayList<>();
    if (closed && openFetch.size() < pageable.getPageSize()) {
      closedFetch = jpaQueryFactory
          .selectFrom(product)
          .leftJoin(product.productImages, productImage).fetchJoin()
          .where(closedBuilderExpression(now), cursorBuilder.and(keywordBuilder))
          .limit(closedProductPageSize(open, openFetch.size(), pageable.getPageSize()))
          .orderBy(product.startDateTime.asc())
          .fetch();
    }

    if (!hasNext && openFetch.size() + closedFetch.size() > pageable.getPageSize()) {
      closedFetch.remove(pageable.getPageSize() - openFetch.size());
      hasNext = true;
    }

    // 구매 가능 상품 목록과 마감된 상품 목록을 합치기
    List<Product> combinedProducts = new ArrayList<>();
    combinedProducts.addAll(openFetch);
    combinedProducts.addAll(closedFetch);

    // ProductSummaryDto 로 변환
    List<ProductSummaryDto> productSummaryDtoList = new ArrayList<>();
    for (Product product : combinedProducts) {
      ProductSummaryDto productSummaryDto = ProductSummaryDto.from(product);
      productSummaryDtoList.add(productSummaryDto);
    }

    return new SliceImpl<>(productSummaryDtoList, pageable, hasNext);
  }

  private long closedProductPageSize(boolean open, int openSize, int pageSize) {
    // 마감 된 상품의 조회 개수 설정
    if (open && openSize < pageSize) { // 구매 가능 상품과 함께 조회하여 반환해야 할 경우
      return pageSize - openSize +1;
    } else {
      return pageSize + 1;
    }
  }

  private BooleanExpression openBuilderExpression(LocalDateTime now) {
    // 압찰을 진행하지 않았거나, 입찰 마감 시간이 남은 상품
    return product.maxEndDateTime.after(now)
            .and(product.dynamicEndDateTime.isNull()
                .or(product.dynamicEndDateTime.after(now)
                )
            );
  }

  private BooleanExpression closedBuilderExpression(LocalDateTime now) {
    // 입찰 마감 시간이 지난 상품
    return product.maxEndDateTime.before(now)
        .or(product.dynamicEndDateTime.isNotNull().and(product.dynamicEndDateTime.before(now)));
  }
}