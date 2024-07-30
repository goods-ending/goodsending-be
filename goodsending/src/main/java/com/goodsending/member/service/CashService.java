package com.goodsending.member.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.CashRequestDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashService {


  private final MemberRepository memberRepository;

  /**
   * 캐시 충전
   * <p>
   * 로그인 한 회원은 입력한 금액 만큼 캐시를 충전 할 수 있다.
   *
   * @param 로그인 한 회원 memberId, CashRequestDto
   * @return status 상태코드 반환합니다.
   * @author : 이아람
   */
  @Transactional
  public ResponseEntity<Void> updateCash(Long pathMemberId, Long memberId,
      CashRequestDto cashRequestDto) {
    if (!pathMemberId.equals(memberId)) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }
    Member member = findByMemberId(memberId);
    member.cashUpdate(cashRequestDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // memberId 검색
  private Member findByMemberId(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
  }
}
