package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductInfoDto {

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
  private List<ProductImageInfoDto> productImages;

  public boolean getHasBidder() {
    return biddingCount > 0;
  }

  @Builder
  public ProductInfoDto(Long productId, Long memberId, String name, int price, String introduction,
      LocalDateTime startDateTime, LocalDateTime dynamicEndDateTime, LocalDateTime maxEndDateTime,
      int biddingCount, int sellingPrice, List<ProductImageInfoDto> productImages) {
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
    this.productImages = productImages;
  }

  public static ProductInfoDto of(Product product, List<ProductImage> productImageList, int sellingPrice) {
    List<ProductImageInfoDto> productImages = new ArrayList<>();
    for (ProductImage productImage : productImageList) {
      ProductImageInfoDto productImageInfoDto = ProductImageInfoDto.from(productImage);
      productImages.add(productImageInfoDto);
    }

    return ProductInfoDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .startDateTime(product.getStartDateTime())
        .dynamicEndDateTime(product.getDynamicEndDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .biddingCount(product.getBiddingCount())
        .sellingPrice(sellingPrice)
        .productImages(productImages)
        .build();
  }

}
