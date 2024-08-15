package com.goodsending.member.controller;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.member.dto.request.CashRequestDto;
import com.goodsending.member.dto.request.PasswordRequestDto;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.dto.response.MemberInfoDto;
import com.goodsending.member.service.MemberService;
import com.goodsending.member.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 이아람
 * @Date : 2024. 07. 23. / 2024. 07. 29
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;
  private final JwtUtil jwtUtil;

  /**
   * 회원가입
   * <p>
   * 이메일 인증, 인증코드, 비밀번호 입력하면 회원가입 된다.
   *
   * @param SignupRequestDto
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "회원 가입 기능", description = "이메일, 인증코드, 비밀번호 입력하면 회원 가입 된다.")
  @PostMapping("/members/signup")
  public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {

    return memberService.signup(signupRequestDto);
  }

  /**
   * 회원 정보 조회
   * <p>
   * 로그인 한 회원의 이메일, 캐시, 포인트, 권한을 조회할 수 있다.
   *
   * @param 로그인 한 유저의 memberId
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "회원 정보 조회 기능", description = "로그인 한 회원 이메일, 캐시, 포인트, 권한 조회")
  @GetMapping("/members/info")
  public ResponseEntity<MemberInfoDto> getMemberInfo(@MemberId Long memberId) {

    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }

  /**
   * 회원 비밀번호 변경
   * <p>
   * 로그인 한 회원의 비밀번호를 변경 할 수 있다.
   *
   * @param 로그인 한 유저의 memberId, PasswordRequestDto
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "비밀번호 변경 기능", description = "로그인 한 회원 비밀번호 변경")
  @PutMapping("/members/{memberId}/password")
  public ResponseEntity<Void> updatePassword(@PathVariable("memberId") Long pathMemberId,
      @MemberId Long memberId,
      @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
    return memberService.updatePassword(pathMemberId, memberId, passwordRequestDto);
  }

  /**
   * 캐시 충전
   * <p>
   * 로그인 한 회원은 입력한 금액 만큼 캐시를 충전 할 수 있다.
   *
   * @param 로그인 한 회원 memberId, CashRequestDto
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "캐시 충전 기능", description = "입력한 금액 만큼 캐시 충전")
  @PutMapping("/members/{memberId}/cash")
  public ResponseEntity<Void> updateCash(@PathVariable("memberId") Long pathMemberId,
      @MemberId Long memberId, @RequestBody CashRequestDto cashRequestDto) {
    return memberService.updateCash(pathMemberId, memberId, cashRequestDto);
  }

  /**
   * Access Token 재발급
   * <p>
   * Access Token이 만료 된 회원은 Refresh Token 기간이 남아 있다면 재발급 받을 수 있다.
   *
   * @param refreshToken
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "Access Token 재발급 기능", description = "Access Token 재발급")
  @PostMapping("/members/tokenReissue")
  public ResponseEntity<Void> tokenReissue(
      @CookieValue(value = JwtUtil.REFRESH_TOKEN_NAME, required = false) String refreshToken) {
    return memberService.tokenReissue(refreshToken);
  }

  /**
   * 로그아웃
   * <p>
   * 로그아웃 하면 Refresh Token 삭제 & 기존에 발급받은 Access Token 사용 할 수 없다.
   *
   * @param refreshToken, HttpServletRequest, HttpServletResponse
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "로그아웃 기능", description = "AccessToken, Refresh Token 삭제")
  @DeleteMapping("/members/logout")
  public ResponseEntity<Void> deleteRefreshToken(
      @CookieValue(value = JwtUtil.REFRESH_TOKEN_NAME, required = false) String refreshToken,
      HttpServletRequest request, HttpServletResponse response) {
    return memberService.deleteRefreshToken(refreshToken, request, response);
  }

  /**
   * Access Token 만료 여부 확인
   * <p>
   * Access Token 단순 만료 여부 확인
   *
   * @param HttpServletRequest
   * @return String 결과 값을 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "Access Token 만료 여부 확인", description = "Access Token 만료 여부 확인")
  @GetMapping("/members/validateAccessToken")
  public ResponseEntity<String> validateAccessToken(HttpServletRequest request) {
    String accessToken = jwtUtil.getJwtFromHeader(request);
    if (accessToken != null) {
      boolean isValid = jwtUtil.validateToken(accessToken);
      if (isValid) {
        log.info("유효한 토큰");
        return ResponseEntity.ok("유효한 토큰입니다.");
      }
    }
    // 유효하지 않은 토큰에 대한 처리는 자동으로 이루어짐
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}


