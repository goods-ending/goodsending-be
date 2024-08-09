package com.goodsending.global.websocket.handler;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service("sendCommandHandler")
@RequiredArgsConstructor
public class SendCommandHandler implements StompCommandHandler{
  private final JwtUtil jwtUtil;
  private final UserDetailsService memberDetailsService;

  @Override
  public void handle(StompHeaderAccessor accessor) {
    String accessToken = resolveAccessTokenFromStompHeaderAccessor(accessor);
    log.info("[ConnectCommandHandler][handle]" + accessToken);

    if (!jwtUtil.validateToken(accessToken)) {
      throw CustomException.from(ExceptionCode.INVALID_TOKEN);
    }

    accessor.setUser(createAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject()));
  }

  private String resolveAccessTokenFromStompHeaderAccessor(StompHeaderAccessor accessor) {
    String authorizationValue = accessor.getFirstNativeHeader(JwtUtil.AUTHORIZATION_HEADER);
    if (!ObjectUtils.isEmpty(authorizationValue)
        && authorizationValue.startsWith(JwtUtil.BEARER_PREFIX)) {
      return authorizationValue.substring(JwtUtil.BEARER_PREFIX.length());
    }
    return null;
  }

  private Authentication createAuthentication(String email) {
    UserDetails userDetails = memberDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
  }
}
