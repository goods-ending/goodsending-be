package com.goodsending.order.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 커서 기반으로 멤버별 주문 내역을 조회하기 위한 필드를 담은 dto
 * @Date : 2024. 08. 16.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record OrderListBySellerRequest(
    Long loginMemberId,

    Long memberId,

    Long cursorId,

    int pageSize
) {
  public Pageable getPageable() {
    return PageRequest.of(0, this.pageSize);
  }
}
