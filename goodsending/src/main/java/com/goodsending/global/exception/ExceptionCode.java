package com.goodsending.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

  // BAD_REQUEST:400:잘못된요청
  INSUFFICIENT_USER_CASH(BAD_REQUEST, "입력한 금액이 유저 캐시보다 큽니다."),
  INSUFFICIENT_USER_POINT(BAD_REQUEST, "입력한 금액이 유저 포인트보다 큽니다."),
  INSUFFICIENT_BID_AMOUNT(BAD_REQUEST, "경매 기본가가 입력한 금액보다 큽니다."),
  BID_AMOUNT_LESS_THAN_CURRENT_MAX(BAD_REQUEST, "현재 최고 입찰 금액이 입력한 금액보다 큽니다."),
  USER_CASH_MUST_BE_POSITIVE(BAD_REQUEST, "유저 캐시는 양수여야 합니다."),
  USER_POINT_MUST_BE_POSITIVE(BAD_REQUEST, "유저 포인트는 양수여야 합니다."),
  AUCTION_ALREADY_WON(BAD_REQUEST, "이미 낙찰된 경매입니다."),
  AUCTION_ALREADY_CLOSED(BAD_REQUEST, "이미 마감된 경매입니다."),
  AUCTION_NOT_STARTED(BAD_REQUEST, "경매가 아직 시작되지 않았습니다."),

  // Unauthorized:401:인증이슈
  INVALID_TOKEN(UNAUTHORIZED, "토큰이 유효하지 않습니다."),

  // FORBIDDEN:403:권한이슈

  // NOT_FOUND:404:자원없음,
  USER_NOT_FOUND(NOT_FOUND, "유저 개체를 찾지 못했습니다."),
  PRODUCT_NOT_FOUND(NOT_FOUND, "경매 상품 개체를 찾지 못했습니다."),
  STOMP_HEADER_ACCESSOR_NOT_FOUND_EXCEPTION(NOT_FOUND, "메시지에서 STOMP 헤더 접근자를 가져오지 못했습니다."),

  // PAYLOAD_TOO_LARGE:413:파일 크기 초과
  FILE_SIZE_EXCEEDED(PAYLOAD_TOO_LARGE, "파일 크기가 10MB를 초과했습니다."),

  // INTERNAL_SERVER_ERROR:500:서버 문제 발생
  LOW_DISK_SPACE(INTERNAL_SERVER_ERROR, "디스크 공간이 부족합니다."),
  FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "파일 변환에 실패했습니다.");

  private final HttpStatus status;
  private final String message;

}