package com.goodsending.member.util;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.type.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Access_Token";
  // Refresh 토큰 이름
  public static final String REFRESH_TOKEN_NAME = "Refresh_Token";
  // 사용자 권한 값의 KEY
  public static final String AUTHORIZATION_KEY = "auth";
  // 사용자 식별자 값의 KEY
  public static final String MEMBER_ID_KEY = "memberId";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // Access 토큰 만료시간
  private final long TOKEN_TIME = 1000L * 60 * 30; // 30분
  // Refresh 토큰
  public static final long REFRESH_TOKEN_TIME = 1000L * 60 * 60 * 24 * 14; // 14일

  @Value("${spring.jwt.secret}") // Base64 Encode 한 SecretKey
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    if (secretKey == null || secretKey.isEmpty()) {
      log.error("JWT secret key is not set!");
      throw new IllegalStateException("JWT secret key is not set!");
    }
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // Access 토큰 생성
  public String createToken(Long memberId, String email, MemberRole role) {
    Date date = new Date();

    return
        Jwts.builder()
            .setSubject(email) // 사용자 식별자값(ID)
            .claim(MEMBER_ID_KEY, memberId) // 사용자 ID 추가
            .claim(AUTHORIZATION_KEY, role) // 사용자 권한
            .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘
            .compact();
  }

  // Refresh 토큰 생성
  public String createRefreshToken(String email) {
    Date date = new Date();

    return Jwts.builder()
        .setSubject(email) // 사용자 식별자값(ID)
        .claim("token_type", "refresh") // 토큰 종류 추가
        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME)) // 만료 시간
        .setIssuedAt(date) // 발급일
        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
        .compact();
  }

  // header 에서 JWT 가져오기
  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
      throw CustomException.from(ExceptionCode.INVALID_SIGNATURE);
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token, 만료된 JWT token 입니다.");
      throw CustomException.from(ExceptionCode.EXPIRED_JWT_TOKEN);
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
      throw CustomException.from(ExceptionCode.UNSUPPORTED_TOKEN);
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
      throw CustomException.from(ExceptionCode.JWT_CLAIMS_ARE_EMPTY);
    }
  }

  // 토큰에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  // Access token 만료시간 가져오기
  public long getExpirationTime(String token) {
    Claims claims = getUserInfoFromToken(token);
    return claims.getExpiration().getTime();
  }
}
