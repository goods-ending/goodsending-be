package com.goodsending.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductWithSellingPriceDto {

  private Long productId;
  private Long memberId;
  private String name;
  private int price;
  private String introduction;
  private LocalDateTime startDateTime;
  private LocalDateTime dynamicEndDateTime;
  private LocalDateTime maxEndDateTime;
  private int biddingCount;
  private int sellingPrice;

  @QueryProjection
  public ProductWithSellingPriceDto(Long productId, Long memberId, String name, int price,
      String introduction, LocalDateTime startDateTime, LocalDateTime dynamicEndDateTime,
      LocalDateTime maxEndDateTime, int biddingCount, int sellingPrice) {
    this.productId = productId;
    this.memberId = memberId;
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.startDateTime = startDateTime;
    this.dynamicEndDateTime = dynamicEndDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.biddingCount = biddingCount;
    this.sellingPrice = sellingPrice;
  }
}
