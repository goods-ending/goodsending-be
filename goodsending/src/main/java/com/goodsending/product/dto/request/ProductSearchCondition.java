package com.goodsending.product.dto.request;

import com.goodsending.product.type.ProductStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductSearchCondition {

  private Long memberId;

  private boolean openProduct;

  private boolean closedProduct;

  private String keyword;

  private ProductStatus cursorStatus;

  private LocalDateTime cursorStartDateTime;

  private Long cursorId;

  private int size;

}
