package com.goodsending.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]{8,15}$")
  private String password;

  @NotBlank
  private String phone_number;

  //private boolean admin = false;
  //private String adminToken = "";
}
