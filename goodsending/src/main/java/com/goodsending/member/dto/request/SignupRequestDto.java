package com.goodsending.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

  @NotBlank(message = "이메일이 입력되지 않았습니다.")
  @Email
  private String email;

  @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
  @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]{8,15}$")
  private String password;

  @NotBlank(message = "비밀번호가 일치하지 않습니다.")
  private String confirmPassword; // DB x, 단순 체크 용

  private boolean admin = false;

  private boolean verify; // 인증 상태

  // TODO : 관리자 할 경우 adminToken
  //private String adminToken = "";
}
