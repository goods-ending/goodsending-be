package com.goodsending.member.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.member.type.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
  private Long member_id;

  @Column(name = "email", nullable = false, unique = true, length = 40)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @Column(name = "phone_number", nullable = false, length = 14)
  private String phone_number;

  @Column(name = "cash", nullable = true)
  private Long cash;

  @Column(name = "point", nullable = true)
  private Long point;

  @Column(name = "code", nullable = true)
  private Long code;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private MemberRole role; // 권한 (ADMIN, USER)

  public Member(String email, String password, String phone_number, MemberRole role) {
    this.email = email;
    this.password = password;
    this.phone_number = phone_number;
    this.role = role;
  }

}
