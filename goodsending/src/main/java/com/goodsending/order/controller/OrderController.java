package com.goodsending.order.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  @Operation(summary = "주문 상품 수신자 정보 업데이트",
      description = "주문 상품 수신자 정보(수신자명, 수신자연락처, 수신자 주소)를 업데이트 합니다.")
  @PutMapping("/receiver-info")
  public ResponseEntity<ReceiverInfoResponse> updateReceiverInfo(
      @MemberId Long memberId, @RequestBody ReceiverInfoRequest request){
    return ResponseEntity.ok(orderService.updateReceiverInfo(memberId, request));
  }
}
