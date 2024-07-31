package com.goodsending.member.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.SignupRequestDto;
import com.goodsending.member.type.MemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "email", nullable = false, unique = true, length = 40)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @Column(name = "phone_number", nullable = false, length = 14)
  private String phoneNumber;

  @Column(name = "cash", nullable = true)
  private Integer cash;

  @Column(name = "point", nullable = true)
  private Integer point;

  @Column(name = "code", nullable = true)
  private Long code;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private MemberRole role; // 권한 (ADMIN, USER)

  @Version
  private Long version;

  @Builder
  private Member(String email, String password, String phoneNumber, MemberRole role) {
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.role = role;
  }

  public static Member from(SignupRequestDto signupRequestDto, String encodedPassword,
      MemberRole role) {
    return Member.builder()
        .email(signupRequestDto.getEmail())
        .password(encodedPassword)
        .phoneNumber(signupRequestDto.getPhoneNumber())
        .role(role)
        .build();
  }

  public boolean isCashGreaterOrEqualsThan(Integer amount){
    if (this.cash == null || amount == null) {
      return false;
    }
    return this.cash >= amount;
  }

  public boolean isPointGreaterOrEqualsThan(Integer amount){
    if(this.point == null || amount == null) {
      return false;
    }
    return this.point >= amount;
  }

  public void deductCash(Integer amount) {
    if(this.cash == null) return;
    if(!this.isCashGreaterOrEqualsThan(amount)){
      throw CustomException.from(ExceptionCode.USER_CASH_MUST_BE_POSITIVE);
    }
    this.cash -= amount;
  }

  public void deductPoint(Integer amount) {
    if(this.point == null) return;
    if (!this.isPointGreaterOrEqualsThan(amount)) {
      throw CustomException.from(ExceptionCode.USER_POINT_MUST_BE_POSITIVE);
    }
    this.point -= amount;
  }
}
