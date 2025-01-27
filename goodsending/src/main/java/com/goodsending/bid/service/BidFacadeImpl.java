package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.dto.response.BidWithDurationResponse;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.global.websocket.DestinationPrefix;
import com.goodsending.product.dto.response.ProductRankingDto;
import com.goodsending.product.repository.ProductBidderCountRankingRepository;
import com.goodsending.product.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BidFacadeImpl implements BidFacade {
  private final BidService bidService;
  private final ProductBidPriceMaxRepository productBidPriceMaxRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final ProductBidderCountRankingRepository productBidderCountRankingRepository;
  private final ProductRepository productRepository;

  @Override
  public BidWithDurationResponse create(Long memberId, BidRequest request, LocalDateTime now)
      throws InterruptedException {
    while(true){
      try {
        BidResponse bidResponse = bidService.create(memberId, request, now);

        // 현재 최고 금액을 업데이트 해준다.
        productBidPriceMaxRepository.setValueWithDuration(request.productId(), request.bidPrice());

        // 경매 마감까지 남은 시간: 업데이트 메시지를 보낸다.
        Duration remainingExpiration = productBidPriceMaxRepository.getRemainingExpiration(
            bidResponse.productId());
        messagingTemplate.convertAndSend(
            DestinationPrefix.TIME_REMAINING + bidResponse.productId(),
            remainingExpiration);

        // 입찰자 수 랭킹에 변동 사항 적용
        updateBidderCountRanking(bidResponse);

        return BidWithDurationResponse.of(bidResponse, remainingExpiration);
      } catch (OptimisticLockException | OptimisticLockingFailureException e){
        Thread.sleep(50);
        log.info("OptimisticLockException");
      }
    }
  }

  private void updateBidderCountRanking(BidResponse bidResponse) {
    ProductRankingDto rankingDto = productRepository.findRankingDtoById(bidResponse.productId());

    String key = "RANKING";
    productBidderCountRankingRepository.setZSetValue(key, rankingDto, bidResponse.bidderCount());
  }
}
