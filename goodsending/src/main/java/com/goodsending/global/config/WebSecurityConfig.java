package com.goodsending.global.config;

import com.goodsending.global.security.JwtAuthenticationEntryPoint;
import com.goodsending.global.security.JwtAuthenticationFilter;
import com.goodsending.global.security.JwtAuthorizationFilter;
import com.goodsending.global.security.MemberDetailsServiceImpl;
import com.goodsending.member.repository.BlackListAccessTokenRepository;
import com.goodsending.member.repository.SaveRefreshTokenRepository;
import com.goodsending.member.util.JwtUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtUtil jwtUtil;
  private final MemberDetailsServiceImpl memberDetailsService;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final SaveRefreshTokenRepository saveRefreshTokenRepository;
  private final BlackListAccessTokenRepository blackListAccessTokenRepository;

  @Value("${front.list}")
  private List<String> frontUrls;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil,
        saveRefreshTokenRepository);
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
  }

  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtUtil, memberDetailsService, jwtAuthenticationEntryPoint, blackListAccessTokenRepository);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // CSRF 설정
    http.csrf((csrf) -> csrf.disable());

    // CORS 설정
    http.cors(configuration -> configuration.configurationSource(corsConfigurationSource()));

    // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
    http.sessionManagement((sessionManagement) ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.authorizeHttpRequests((authorizeHttpRequests) ->
        authorizeHttpRequests
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .permitAll() // resources 접근 허용 설정
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/check").permitAll()
            .requestMatchers("/ws", "/api/members/sendMail", "/api/members/signup",
                "/api/members/login", "/api/members/checkCode", "/api/members/tokenReissue").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/likes/top5", "/api/products/**", "/api/product-message-histories", "/api/likes/redis").permitAll()
            //.requestMatchers("/").permitAll()
            .anyRequest().authenticated() // 그 외 모든 요청 인증처리
    );

    http.formLogin((formLogin) ->
        formLogin
            // 로그인 View 제공 (GET)
            //.loginPage("/api/members/login-page")
            // 로그인 처리 (POST)
            .loginProcessingUrl("/api/members/login")
            // 로그인 처리 후 성공 시 URL
            //.defaultSuccessUrl("/")
            // 로그인 처리 후 실패 시 URL
            //.failureUrl("/api/members/login-page?error")
            .permitAll()

    );

    // 필터 관리
    http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    // 예외 처리
    http.exceptionHandling(exceptions -> exceptions
        .authenticationEntryPoint(jwtAuthenticationEntryPoint));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(frontUrls);
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setMaxAge(600L); // 10분
    configuration.setExposedHeaders(Collections.singletonList(JwtUtil.AUTHORIZATION_HEADER));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
