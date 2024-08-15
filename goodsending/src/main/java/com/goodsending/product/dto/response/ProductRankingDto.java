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

  public ProductRankingDto(Product product, ProductImage productImage) {
    this.productId = product.getId();
    this.name = product.getName();
    this.price = product.getPrice();
    this.startDateTime = product.getStartDateTime();
    this.maxEndDateTime = product.getMaxEndDateTime();
    this.status = product.getStatus();
    this.thumbnailUrl = productImage.getUrl();
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

  public static ProductRankingDto from(ProductRankingLikeCountDto dto) {
    return ProductRankingDto.builder()
        .productId(dto.getProductId())
        .name(dto.getName())
        .price(dto.getPrice())
        .startDateTime(dto.getStartDateTime())
        .maxEndDateTime(dto.getStartDateTime())
        .status(dto.getStatus())
        .thumbnailUrl(dto.getThumbnailUrl())
        .build();
  }
}
