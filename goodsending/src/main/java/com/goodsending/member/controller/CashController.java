package com.goodsending.member.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.member.dto.request.CashRequestDto;
import com.goodsending.member.service.CashService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 이아람
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CashController {

  private final CashService cashService;

  /**
   * 캐시 충전
   * <p>
   * 로그인 한 회원은 입력한 금액 만큼 캐시를 충전 할 수 있다.
   *
   * @param 로그인 한 회원 memberId, CashRequestDto
   * @return CashService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "캐시 충전 기능", description = "입력한 금액 만큼 캐시 충전")
  @PutMapping("/members/{memberId}/cash")
  public ResponseEntity<Void> updateCash(@PathVariable("memberId") Long pathMemberId,
      @MemberId Long memberId, @RequestBody CashRequestDto cashRequestDto) {
    return cashService.updateCash(pathMemberId, memberId, cashRequestDto);
  }
}
