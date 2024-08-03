package com.goodsending.order.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public ReceiverInfoResponse updateReceiverInfo(Long memberId, ReceiverInfoRequest request) {
    Order order = findOrderWithBidById(request.orderId());

    if(!order.isReceiverId(memberId)) {
      throw CustomException.from(ExceptionCode.RECEIVER_ID_MISMATCH);
    }

    return ReceiverInfoResponse.from(order.updateReceiverInfo(request));
  }

  private Order findOrderWithBidById(Long orderId) {
    return orderRepository.findOrderWithBidById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );
  }
}
