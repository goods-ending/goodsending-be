package com.goodsending.member.service;

import com.goodsending.member.dto.SignupRequestDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.type.MemberRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // ADMIN_TOKEN
  //private final String ADMIN_TOKEN = "1234";

  // 회원 가입
  public ResponseEntity<?> signup(SignupRequestDto signupRequestDto) {
    String email = signupRequestDto.getEmail();
    String password = passwordEncoder.encode(signupRequestDto.getPassword());
    String phone_number = signupRequestDto.getPhone_number();

    // 이메일 중복 확인
    Optional<Member> checkEmail = memberRepository.findByEmail(email);
    if (checkEmail.isPresent()) {
      throw new IllegalArgumentException("중복된 Email 입니다.");
    }

    // 사용자 ROLE 확인
    MemberRole role = MemberRole.USER;
//        if (signupRequestDto.isAdmin()) {
//            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = MemberRole.ADMIN;
//        }

    // 사용자 등록
    Member member = new Member(email, password, phone_number, role);
    memberRepository.save(member);
    return ResponseEntity.ok("가입 완료");
  }
}
