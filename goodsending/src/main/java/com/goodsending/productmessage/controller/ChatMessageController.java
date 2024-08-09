package com.goodsending.productmessage.controller;

import com.goodsending.global.websocket.DestinationPrefix;
import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.product.dto.request.ChatMessageRequest;
import com.goodsending.productmessage.dto.request.ProductMessageRequest;
import com.goodsending.productmessage.service.ProductMessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {
  private final ProductMessageService productMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/message")
  public void chatToProduct(Principal principal, @Payload ChatMessageRequest request) {
    ProductMessageDto productMessageDto = productMessageService.create(
        ProductMessageRequest.of(request, principal.getName()));

    messagingTemplate.convertAndSend(DestinationPrefix.PRODUCT + request.productId(),
        productMessageDto);
  }

}
