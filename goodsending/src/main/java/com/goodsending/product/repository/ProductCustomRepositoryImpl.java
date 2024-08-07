package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.QProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.type.ProductStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
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
  public Slice<ProductSummaryDto> findByFiltersAndSort(Long memberId, String openProduct, String closedProduct,
      String keyword, ProductStatus cursorStatus, LocalDateTime cursorStartDateTime, Long cursorId, Pageable pageable) {

    boolean open = openProduct != null && openProduct.equals("true");

    boolean closed = closedProduct != null && closedProduct.equals("true");

    // 키워드 검색
    BooleanBuilder keywordBuilder = new BooleanBuilder();
    if (keyword != null && !keyword.isEmpty()) {
      keywordBuilder.and(product.name.containsIgnoreCase(keyword));
    }

    NumberExpression<Integer> statusRank = statusRankCaseBuilder();

    BooleanBuilder cursorBuilder = cursorBuilder(cursorStatus, cursorStartDateTime, cursorId, statusRank);

    BooleanExpression openExpression = product.status.eq(ProductStatus.UPCOMING)
                                              .or(product.status.eq(ProductStatus.ONGOING));

    BooleanExpression closedExpression = product.status.eq(ProductStatus.ENDED);

    JPAQuery<ProductSummaryDto> query = jpaQueryFactory
        .select(new QProductSummaryDto(
            product.id,
            product.name,
            product.price,
            product.startDateTime,
            product.dynamicEndDateTime,
            product.maxEndDateTime,
            product.status,
            productImage.url
        ))
        .from(product)
        .leftJoin(productImage).on(productImage.product.eq(product));

    if (memberId != null) {
      query.where(productImageEq().and(memberIdEq(memberId)))
          .orderBy(statusRank.asc(), product.startDateTime.asc(), product.id.asc())
          .limit(pageable.getPageSize()+1);
    } else if (open == closed) {
      query.where(productImageEq().and(cursorBuilder).and(keywordBuilder))
          .orderBy(statusRank.asc(), product.startDateTime.asc(), product.id.asc())
          .limit(pageable.getPageSize()+1);
    } else if (open) {
      query.where(productImageEq().and(cursorBuilder).and(keywordBuilder).and(openExpression))
          .orderBy(statusRank.asc(), product.startDateTime.asc(), product.id.asc())
          .limit(pageable.getPageSize()+1);
    } else if (closed) {
      query.where(productImageEq().and(cursorBuilder).and(keywordBuilder).and(closedExpression))
          .orderBy(product.startDateTime.asc(), product.id.asc())
          .limit(pageable.getPageSize()+1);
    }

    List<ProductSummaryDto> productSummaryDtoList = query.fetch();

    boolean hasNext = false;
    if (productSummaryDtoList.size() > pageable.getPageSize()) {
      hasNext = true;
      productSummaryDtoList.remove(pageable.getPageSize());
    }

    return new SliceImpl<>(productSummaryDtoList, pageable, hasNext);
  }

  @Override
  public List<Product> findAllByStatusAndStartDateTime(ProductStatus status, LocalDateTime startDateTime) {

    BooleanBuilder startDateTimeEq = new BooleanBuilder();
    if (status.equals(ProductStatus.UPCOMING)) {
      startDateTimeEq.and(product.startDateTime.eq(startDateTime));
    }

    return jpaQueryFactory
        .selectFrom(product)
        .where(product.status.eq(status).and(startDateTimeEq))
        .fetch();
  }

  @Override
  public List<ProductSummaryDto> findTop5ByBiddingCount() {
    return jpaQueryFactory.select(new QProductSummaryDto(
            product.id,
            product.name,
            product.price,
            product.startDateTime,
            product.dynamicEndDateTime,
            product.maxEndDateTime,
            product.status,
            productImage.url))
        .from(product)
        .leftJoin(productImage).on(productImage.product.eq(product))
        .where(productImageEq().and(product.status.eq(ProductStatus.ONGOING)))
        .orderBy(product.biddingCount.desc())
        .limit(5)
        .fetch();
  }

  @Override
  public List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(
      LocalDateTime currentDateTime) {
    return jpaQueryFactory.selectFrom(product)
        .where(product.startDateTime.gt(currentDateTime))
        .orderBy(product.likeCount.desc())
        .limit(5)
        .fetch();
  }

  private BooleanExpression memberIdEq(Long memberId) {
    return product.member.memberId.eq(memberId);
  }

  private BooleanExpression productImageEq(){
    return productImage.id.eq(
        JPAExpressions
            .select(productImage.id.min())
            .from(productImage)
            .where(productImage.product.eq(product)));
  }

  private NumberExpression<Integer> statusRankCaseBuilder() {
    NumberExpression<Integer> statusRank = new CaseBuilder()
        .when(product.status.eq(ProductStatus.ONGOING)).then(ProductStatus.ONGOING.getRank())
        .when(product.status.eq(ProductStatus.UPCOMING)).then(ProductStatus.UPCOMING.getRank())
        .when(product.status.eq(ProductStatus.ENDED)).then(ProductStatus.ENDED.getRank())
        .otherwise(4);
    return statusRank;
  }

  private BooleanBuilder cursorBuilder(ProductStatus cursorStatus, LocalDateTime cursorStartDateTime, Long cursorId, NumberExpression<Integer> statusRank) {
    BooleanBuilder cursorBuilder = new BooleanBuilder();
    if (cursorStatus != null && cursorStartDateTime != null && cursorId != null) {
      cursorBuilder.and(
          statusRank.gt(cursorStatus.getRank())
              .or(statusRank.eq(cursorStatus.getRank()).and(product.startDateTime.gt(cursorStartDateTime)))
              .or(statusRank.gt(cursorStatus.getRank()).and(product.startDateTime.eq(cursorStartDateTime)).and(product.id.gt(cursorId)))
      );
    }
    return cursorBuilder;
  }
}
