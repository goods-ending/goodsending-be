package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.type.ProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductRankingDto {

  private Long productId;
  private String name;
  private int price;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private ProductStatus status;
  private String thumbnailUrl;

  @Builder
  @QueryProjection
  public ProductRankingDto(Long productId, String name, int price, LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, ProductStatus status, String thumbnailUrl) {
    this.productId = productId;
    this.name = name;
    this.price = price;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.status = status;
    this.thumbnailUrl = thumbnailUrl;
  }

  public static ProductRankingDto of(Product product, ProductImage productImage) {
    return ProductRankingDto.builder()
        .productId(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .startDateTime(product.getStartDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .status(product.getStatus())
        .thumbnailUrl(productImage.getUrl())
        .build();
  }
}
