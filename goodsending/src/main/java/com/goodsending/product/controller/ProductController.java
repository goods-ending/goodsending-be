package com.goodsending.product.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * @Date : 2024. 07. 23.
 * @Team : GoodsEnding
 * @author : puclpu
 * @Project : goodsending-be :: goodsending
 */

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  /**
   * 경매 상품 등록
   *
   * 사용자가 경매 상품을 등록합니다
   *
   * @param requestDto 상품 정보
   * @param productImages 상품 이미지
   * @param memberId 등록자
   * @return 생성된 상품 정보 반환
   * @author : puclpu
   */
  @PostMapping
  public ResponseEntity<ProductCreateResponseDto> createProduct(
      @RequestPart("requestDto") @Valid ProductCreateRequestDto requestDto,
      @RequestPart("productImages") List<MultipartFile> productImages,
      @MemberId(required = true) Long memberId) {
    ProductCreateResponseDto responseDto = productService.createProduct(requestDto, productImages,
        memberId);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  /**
   * 선택한 경매 상품 상세 정보 조회
   * @param productId 상품 아이디
   * @param productId
   * @return 경매 상품 상세 정보 반환
   * @author : puclpu
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ProductInfoDto> getProduct(@PathVariable Long productId) {
    ProductInfoDto responseDto = productService.getProduct(productId);
    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  /**
   * 경매 상품 검색
   * @param keyword 검색어
   * @return 키워드 검색을 통해 조회한 경매 상품 목록 페이지 반환
   * @author : puclpu
   */
  @GetMapping()
  public ResponseEntity<Page<ProductSummaryDto>> getProductList(@RequestParam(required = false) String keyword,
                                                                @RequestParam(required = true) int page,
                                                                @RequestParam(required = true)int size) {
    Page<ProductSummaryDto> responseDtoList = productService.getProductList(keyword, page-1, size);
    return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
  }

}