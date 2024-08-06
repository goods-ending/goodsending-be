package com.goodsending.product.scheduler;

import com.goodsending.product.service.ProductService;
import com.goodsending.product.type.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

  private final ProductService productService;

  @Scheduled(cron = "0 0 12,18 * * *") // 매일 12시, 18시
  public void updateUpComingProduct() {
    log.info("경매 진행 상태 전환 ");
    productService.updateProductStatus(ProductStatus.UPCOMING);
  }

  @Scheduled(cron = "59 18 14,20 * * *") // 매일 14시, 20시 59분 59초
  public void updateOnGoingProduct() {
    log.info("경매 종료 상태 전환");
    productService.updateProductStatus(ProductStatus.ONGOING);
  }

}
