package com.goodsending.productmessage.service;

import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.productmessage.dto.request.ProductMessageListRequest;
import com.goodsending.productmessage.dto.request.ProductMessageRequest;
import com.goodsending.productmessage.dto.response.ProductMessageResponse;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import org.springframework.data.domain.Slice;

public interface ProductMessageService {

  ProductMessageDto create(CreateProductMessageEvent event);

  ProductMessageDto create(ProductMessageRequest request);

  Slice<ProductMessageResponse> read(ProductMessageListRequest request);
}
