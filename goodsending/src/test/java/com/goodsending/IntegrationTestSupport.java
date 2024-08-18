package com.goodsending;

import com.goodsending.global.config.MailConfig;
import com.goodsending.global.config.S3Config;
import com.goodsending.global.config.WebSecurityConfig;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.global.websocket.handler.SendCommandHandler;
import com.goodsending.member.util.JwtUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @MockBean
  protected MailConfig mailSender;

  @MockBean
  protected S3Config s3Config;

  @MockBean
  protected WebSecurityConfig securityConfig;

  @MockBean
  protected S3Uploader s3Uploader;

  @MockBean
  protected SendCommandHandler sendCommandHandler;

  @MockBean
  protected JavaMailSender javaMailSender;

  @MockBean
  protected PasswordEncoder passwordEncoder;

  @MockBean
  protected JwtUtil jwtUtil;
}
