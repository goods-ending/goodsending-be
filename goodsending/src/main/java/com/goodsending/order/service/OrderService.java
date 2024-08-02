package com.goodsending.order.service;

import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;

/**
 * @author : jieun
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
public interface OrderService {

  ReceiverInfoResponse updateReceiverInfo(Long memberId, ReceiverInfoRequest request);
}
