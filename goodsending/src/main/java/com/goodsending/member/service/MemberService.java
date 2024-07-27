package com.goodsending.member.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.MemberDetailsDto;
import com.goodsending.member.dto.MemberInfoDto;
import com.goodsending.member.dto.SignupRequestDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.member.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;
  private final JwtUtil jwtUtil;

  // TODO : 관리자 할 경우 ADMIN_TOKEN 생성
  //private final String ADMIN_TOKEN = "1234";

  // 회원 가입
  public ResponseEntity<?> signup(SignupRequestDto signupRequestDto) {
    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

    // 이메일 중복 확인
    Optional<Member> checkEmail = memberRepository.findByEmail(signupRequestDto.getEmail());
    if (checkEmail.isPresent()) {
      throw new IllegalArgumentException("중복된 Email 입니다.");
    }

    // TODO : 관리자 할 경우 사용자 ROLE 확인
    MemberRole role = MemberRole.USER;
//        if (signupRequestDto.isAdmin()) {
//            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = MemberRole.ADMIN;
//        }

    Member member = Member.from(signupRequestDto, encodedPassword, role);
    memberRepository.save(member);
    return ResponseEntity.ok("가입 완료");
  // 회원 정보 조회
  public MemberInfoDto getMemberInfo(Long memberId) {
    MemberDetailsDto memberDetailsDto = memberRepository.findByMemberId(memberId);
    MemberRole role = memberDetailsDto.getRole();
    boolean isAdmin = (role == MemberRole.ADMIN);
    return new MemberInfoDto(memberDetailsDto, isAdmin);
  }
}
