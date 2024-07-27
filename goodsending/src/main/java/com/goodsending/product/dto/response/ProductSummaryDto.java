package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductSummaryDto {

  private Long productId;
  private String name;
  private int price;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDate;
  private String thumbnailUrl;
  // TODO : 입찰 여부 필드

  @Builder
  public ProductSummaryDto(Long productId, String name, int price, LocalDateTime startDateTime,
      LocalDateTime maxEndDate, String thumbnailUrl) {
    this.productId = productId;
    this.name = name;
    this.price = price;
    this.startDateTime = startDateTime;
    this.maxEndDate = maxEndDate;
    this.thumbnailUrl = thumbnailUrl;
  }

  public static ProductSummaryDto of(Product product, ProductImage productImage) {
    return ProductSummaryDto.builder()
        .productId(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .startDateTime(product.getStartDateTime())
        .maxEndDate(product.getMaxEndDateTime())
        .thumbnailUrl(productImage.getUrl())
        .build();
  }
}
