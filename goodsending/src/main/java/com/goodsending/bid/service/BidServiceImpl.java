package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import com.goodsending.bid.entity.Bid;
import com.goodsending.bid.repository.BidRepository;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.entity.Product;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import com.goodsending.productmessage.type.MessageType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

  private final BidRepository bidRepository;
  private final ProductRepository productRepository;
  private final MemberRepository memberRepository;
  private final ProductBidPriceMaxRepository productBidPriceMaxRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * 입찰 신청
   * <p>
   * 유저가 지닌 캐시와 포인트로 결제하여 입찰을 합니다.
   *
   * @param memberId 입찰자 id
   * @param request  입찰 정보
   * @return 생성된 입찰 정보를 반환합니다.
   * @author : jieun(je-pa)
   */
  @Override
  @Transactional
  public BidResponse create(Long memberId, BidRequest request, LocalDateTime now) {
    Product product = findProductWithOptimisticLock(request.productId());

    // 현재 시간이 입찰 가능한 시간인지 확인한다.
    validAuctionTimeOrThrow(product, now);

    // 입찰 신청 금액 검사
    validBidPriceOrThrow(product, request.bidPrice());

    Member member = findMember(memberId);

    // 유저의 캐시와 포인트가 차감된다.
    processPayment(member, request.bidPrice(), request.usePoint());

    // 입찰 내역이 생성된다.
    Bid save = bidRepository.save(Bid.of(member, product, request));

    // 입찰수 변경
    product.setBiddingCount(bidRepository.countByProduct(product.getId()));
    // 입찰자 수 변경
    product.setBidderCount(bidRepository.countDistinctMembersByProduct(product.getId()));

    // 입찰 메시지 이벤트 발행
    eventPublisher.publishEvent(CreateProductMessageEvent.of(
        memberId, request.productId(),
        MessageType.BID,
        request.bidPrice()));

    return BidResponse.from(save);
  }

  /**
   * 멤버별 입찰 내역 리스트 조회
   * @param request 조회에 사용되는 필드들을 담은 dto
   * @return 커서기반 페이징 처리된 입찰 내역 리스트
   * @author : jieun(je-pa)
   */
  @Override
  @Transactional(readOnly = true)
  public Slice<BidWithProductResponse> readByMember(BidListByMemberRequest request) {
    if(request.loginMemberId() != request.memberId()){
      throw CustomException.from(ExceptionCode.ONLY_SELF_ACCESS);
    }
    return bidRepository.findBidWithProductResponseList(request);
  }

  private void validBidPriceOrThrow(Product product, Integer amount) {
    Long productId = product.getId();

    if (productBidPriceMaxRepository.hasKey(productId)
        && productBidPriceMaxRepository.isBidPriceMaxGreaterOrEqualsThan(productId, amount)) {
      // 현재 최고 입찰 금액이 입력값보다 크거나 같으면 안된다. - 동시성 이슈
      throw CustomException.from(ExceptionCode.BID_AMOUNT_LESS_THAN_CURRENT_MAX);
    }

    // 입찰 최소 금액이 입력값보다 크거나 같으면 안된다.
    if (product.isPriceGreaterOrEqualsThan(amount)) {
      throw CustomException.from(ExceptionCode.INSUFFICIENT_BID_AMOUNT);
    }
  }

  private void validAuctionTimeOrThrow(Product product, LocalDateTime now) {
    if(product.getDynamicEndDateTime() != null) {
      // dynamic date time - 동시성 문제
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_WON);
    }
    if(now.isBefore(product.getStartDateTime())) {
      throw CustomException.from(ExceptionCode.AUCTION_NOT_STARTED);
    }
    if(now.isAfter(product.getMaxEndDateTime())) {
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_CLOSED);
    }
  }

  private void processPayment(Member member, Integer price, Integer pointAmount) {
    if(price < pointAmount) {
      throw CustomException.from(ExceptionCode.EXCESSIVE_POINT);
    }
    Integer cacheAmount = price;
    if (pointAmount != null && !member.isPointGreaterOrEqualsThan(pointAmount)) {
      // 동시성 문제
      throw CustomException.from(ExceptionCode.INSUFFICIENT_USER_POINT);
    }
    cacheAmount -= pointAmount == null ? 0 : pointAmount;
    if (cacheAmount != null && !member.isCashGreaterOrEqualsThan(cacheAmount)) {
      // 동시성 문제
      throw CustomException.from(ExceptionCode.INSUFFICIENT_USER_CASH);
    }

    member.deductCash(cacheAmount);
    member.deductPoint(pointAmount);
  }

  private Member findMember(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
  }

  private Product findProductWithOptimisticLock(Long productId) {
    return productRepository.findByIdWithOptimisticLock(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));
  }
}
