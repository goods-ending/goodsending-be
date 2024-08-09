package com.goodsending.product.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductSearchCondition;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductRankingDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductUpdateResponseDto;
import com.goodsending.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
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
  @Operation(summary = "경매 상품 등록 기능", description = "상품명, 판매가, 상품소개, 경매시작일, 경매시간대, 상품 이미지를 입력하면 상품을 등록할 수 있다.")
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
  @Operation(summary = "경매 상품 상세 정보 조회 기능", description = "상품 아이디를 통해 선택한 상품의 상세 정보를 조회할 수 있다.")
  @GetMapping("/{productId}")
  public ResponseEntity<ProductInfoDto> getProduct(@PathVariable Long productId) {
    ProductInfoDto responseDto = productService.getProduct(productId);
    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  /**
   * 경매 상품 목록 조회
   * @param productSearchCondition 경매 상품 목록 조회 조건
   * @return 조회한 경매 상품 목록
   * @author : puclpu
   */
  @Operation(summary = "경매 상품 검색 기능",
      description = "전체 조회일 경우 조건없이, "
          + "내가 등록한 상품 목록 조회 시 memberId를, "
          + "필터링 검색의 경우 openProduct, closedProduct, keyword 를 조건으로 상품 목록을 조회한다.")
  @GetMapping
  public ResponseEntity<Slice<ProductSummaryDto>> getProductSlice(@RequestBody ProductSearchCondition productSearchCondition) {
    Slice<ProductSummaryDto> productSummaryDtoSlice = productService.getProductSlice(productSearchCondition);
    return ResponseEntity.status(HttpStatus.OK).body(productSummaryDtoSlice);
  }

  /**
   * 경매 상품 수정
   * @param productId 상품 아이디
   * @param requestDto 상품 수정 요청 정보
   * @param productImages 상품 이미지
   * @param memberId 등록자
   * @return 수정된 상품 정보
   * @author : puclpu
   */
  @Operation(summary = "경매 상품 수정 기능", description = "상품 아이디를 통해 상품명, 상품 소개, 경매시작일, 경매 시간대를 수정할 수 있습니다.")
  @PutMapping("/{productId}")
  public ResponseEntity<ProductUpdateResponseDto> updateProduct (@PathVariable Long productId,
                                    @RequestPart("requestDto") @Valid ProductUpdateRequestDto requestDto,
                                    @RequestPart("productImages") List<MultipartFile> productImages,
                                    @MemberId(required = true) Long memberId) {
    LocalDateTime now = LocalDateTime.now();
    ProductUpdateResponseDto responseDto = productService.updateProduct(productId, requestDto, productImages, memberId, now);
    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  /**
   * 경매 상품 삭제
   * @param productId 상품 아이디
   * @param memberId 등록자
   * @return 경매 상품 삭제 성공 여부
   * @author : puclpu
   */
  @Operation(summary = "경매 상품 삭제", description = "상품 아이디와 회원 아이디로 상품을 삭제할 수 있습니다.")
  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId, @MemberId(required = true) Long memberId) {
    LocalDateTime now = LocalDateTime.now();
    productService.deleteProduct(productId, memberId, now);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * 경매 상품 입찰자수 TOP5 조회
   * @return TOP5 상품 목록
   * @author : puclpu
   */
  @Operation(summary = "경매 상품 입찰자 수 TOP5 조회", description = "입찰자 수를 기준으로 상위 top5 상품을 조회합니다.")
  @GetMapping("/top5")
  public ResponseEntity<List<ProductRankingDto>> getTop5Products() {
    List<ProductRankingDto> productRankingDtoList = productService.getTop5Products();
    return ResponseEntity.status(HttpStatus.OK).body(productRankingDtoList);
  }
}