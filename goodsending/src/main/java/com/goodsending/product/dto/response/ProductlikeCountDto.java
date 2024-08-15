package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductlikeCountDto {

  private Long productId;
  private String productName;
  private int price;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private String thumbnailUrl;
  private Long likeCount;

  @Builder
  public ProductlikeCountDto(Long productId, String productName, int price,
      LocalDateTime startDateTime, LocalDateTime maxEndDateTime,
      String thumbnailUrl, Long likeCount) {
    this.productId = productId;
    this.productName = productName;
    this.price = price;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.thumbnailUrl = thumbnailUrl;
    this.likeCount = likeCount;
  }

  public static ProductlikeCountDto from(Long productId, String productName, int price, Long likeCount,
      LocalDateTime startDateTime, LocalDateTime maxEndDateTime, String thumbnailUrl) {
    return ProductlikeCountDto.builder()
        .productId(productId)
        .productName(productName)
        .price(price)
        .startDateTime(startDateTime)
        .maxEndDateTime(maxEndDateTime)
        .thumbnailUrl(thumbnailUrl)
        .likeCount(likeCount)
        .build();
  }

  public static ProductlikeCountDto from(Product product) {
    return ProductlikeCountDto.builder()
        .productId(product.getId())
        .productName(product.getName())
        .price(product.getPrice())
        .startDateTime(product.getStartDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .likeCount(product.getLikeCount())
        .build();
  }

}
