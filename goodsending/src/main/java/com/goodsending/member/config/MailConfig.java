package com.goodsending.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${YOUR_NAVER_EMAIL}")
    private String username;

    @Value("${YOUR_NAVER_PASSWORD}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.naver.com");
        mailSender.setPort(465);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp"); // 프로토콜 설정
        properties.put("mail.smtp.auth", "true"); // smtp 인증
        properties.put("mail.smtp.ssl.enable", "true"); // ssl 사용
        properties.put("mail.smtp.ssl.trust", "smtp.naver.com"); // ssl 인증 서버
        properties.put("mail.debug", "true"); // 디버그 사용

        return mailSender;
    }
}