package com.goodsending.productlike.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class LikeController {

  private final LikeService likeService;

  @Operation(summary = "찜하기 토글 기능", description = "멤버Id, 상품Id, 클릭값 입력하면 찜하기 등록,취소 된다.")
  @PostMapping("/likes")
  public ResponseEntity<LikeResponseDto> toggleLike(@MemberId Long memberId,
      @RequestBody @Valid LikeRequestDto requestDto) {
    return likeService.toggleLike(memberId, requestDto);
  }


  @Operation(summary = "찜한 상품 목록 조회 페이징", description = "회원의 찜한 목록이 조회된다.")
  @GetMapping("/likes")
  public ResponseEntity<Page<ProductCreateResponseDto>> getLikeProductsPage(
      @MemberId Long memberId,
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @RequestParam("sortBy") String sortBy,
      @RequestParam("isAsc") boolean isAsc
  ) {
    return ResponseEntity.ok(
        likeService.getLikeProductsPage(memberId, page - 1, size, sortBy, isAsc));
  }

  @Operation(summary = "찜하기 수 top5 상품 조회", description = "찜하기 수 top5 상품조회 한다.")
  @GetMapping("/likes/top5")
  public ResponseEntity<List<ProductCreateResponseDto>> getTop5LikeProduct(
  ) {
    LocalDateTime dateTime = LocalDateTime.now();
    return ResponseEntity.ok(
        likeService.getTop5LikeProduct(dateTime));
  }

  @Operation(summary = "찜하기 수 top5 상품 등록 redis", description = "찜하기 수 top5 상품등록 한다. redis")
  @PostMapping("/likes/redis")
  public ResponseEntity<LikeResponseDto> create(@MemberId Long memberId,
      @RequestBody @Valid LikeRequestDto requestDto) {
    LocalDateTime dateTime = LocalDateTime.now();
    return likeService.toggleLikeRedis(memberId, requestDto);
  }


  @Operation(summary = "찜하기 수 top5 상품 조회 redis", description = "찜하기 수 top5 상품조회 한다. redis")
  @GetMapping("/likes/redis")
  public ResponseEntity<List<ProductCreateResponseDto>> read() {
    LocalDateTime dateTime = LocalDateTime.now();
    return ResponseEntity.ok(likeService.read(dateTime));
  }
}
