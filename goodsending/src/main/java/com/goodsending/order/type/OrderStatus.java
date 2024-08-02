package com.goodsending.order.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
  CANCELLED("취소된"),
  COMPLETED("완료된"),
  SHIPPING("배송중"),
  PENDING("배송전");

  private final String name;

  public String getName() {
    return name;
  }
}
