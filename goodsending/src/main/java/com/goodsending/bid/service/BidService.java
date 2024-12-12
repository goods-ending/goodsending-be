package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidService {

  BidResponse create(Long memberId, BidRequest request, LocalDateTime now);

  /**
   * 멤버별 입찰 내역 리스트 조회
   * @param request 조회에 사용되는 필드들을 담은 dto
   * @return 커서기반 페이징 처리된 입찰 내역 리스트
   * @author : jieun(je-pa)
   */
  Slice<BidWithProductResponse> readByMember(BidListByMemberRequest request);

  /**
   * 낙찰자 자동 선정 후 주문 생성
   * <p>
   * <p>입찰 후 5분동안 추가 입찰이 없으면 마지막 입찰자가 낙찰자가 됩니다.
   * <p>입찰의 상태가 낙찰자는 SUCCESS, 낙찰자를 제외한 입찰은 FAIL로 업데이트 됩니다.
   * <p>낙찰자의 주문이 자동 생성됩니다.
   * <p>낙찰자를 제외한 유저는 환불처리(포인트, 캐시) 됩니다.
   *
   * @param productId 낙찰로 인해 경매 마감되는 상품 id
   */
  void processBidsOnExpiration(Long productId);
}
