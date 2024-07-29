package com.goodsending.member.dto.response;

import com.goodsending.member.type.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoDto {
  private Long member_id;
  private String email;
  private Integer cash;
  private Integer point;
  private MemberRole role;

  public MemberInfoDto(MemberDetailsDto memberDetailsDto) {
    this.member_id = memberDetailsDto.getMemberId();
    this.email = memberDetailsDto.getEmail();
    this.cash = memberDetailsDto.getCash();
    this.point = memberDetailsDto.getPoint();
    this.role = memberDetailsDto.getRole();
  }

}
