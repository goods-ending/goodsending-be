package com.goodsending.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.deposit.entity.Deposit;
import com.goodsending.deposit.repository.DepositRepository;
import com.goodsending.deposit.type.DepositStatus;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductSearchCondition;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductImageCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductRankingDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductUpdateResponseDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductBidderCountRankingRepository;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.product.type.ProductStatus;
import com.goodsending.productlike.repository.LikeCountRankingRepository;
import com.goodsending.productlike.service.LikeService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @Date : 2024. 07. 23.
 * @Team : GoodsEnding
 * @author : puclpu
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private static String S3_IMAGE_PATH = "image/products";

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final S3Uploader s3Uploader;
  private final MemberRepository memberRepository;
  private final DepositRepository depositRepository;
  private final ProductBidPriceMaxRepository productBidPriceMaxRepository;
  private final ProductBidderCountRankingRepository productBidderCountRankingRepository;
  private final ObjectMapper jacksonObjectMapper;
  private final LikeService likeService;
  private final LikeCountRankingRepository likeCountRankingRepository;

  /**
   * 상품 등록
   * @param requestDto
   * @param productImages
   * @param memberId
   * @return 생성된 상품 정보 반환
   * @author : puclpu
   */
  @Override
  @Transactional
  public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId) {

    // 상품 이미지 개수 초과 판별
    int size = productImages.size();
    if (size > 5) {
      throw CustomException.from(ExceptionCode.FILE_COUNT_EXCEEDED);
    }

    // 존재하는 회원인지 판별
    Member member = findMember(memberId);

    // 상품 정보 저장
    Product product = Product.of(requestDto, member);
    Product savedProduct = productRepository.save(product);

    // 버킷에 상품 이미지 업로드
    List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages, S3_IMAGE_PATH);

    // 업로드 된 상품의 url 저장
    List<ProductImageCreateResponseDto> savedProductImages = new ArrayList<>();
    for (String uploadedFileName : uploadedFileNames) {
      ProductImage productImage = ProductImage.of(product, uploadedFileName);
      productImageRepository.save(productImage);

      ProductImageCreateResponseDto productImageCreateResponseDto = ProductImageCreateResponseDto.from(productImage);
      savedProductImages.add(productImageCreateResponseDto);
    }

    // 보증금 차감
    Integer productPrice = savedProduct.getPrice();
    Integer depositPrice = (int)(productPrice * 0.05); // 보증금은 0.05%를 가져갑니다
    if (depositPrice < 3000) { // 보증금은 최소 3000원
      depositPrice = 3000;
    }
    member.deductCash(depositPrice);

    // 보증금 내역 저장
    Deposit deposit = Deposit.of(savedProduct, member, depositPrice);
    depositRepository.save(deposit);

    return ProductCreateResponseDto.of(savedProduct, savedProductImages);
  }

  /**
   * 선택한 경매 상품 상세 정보 조회
   * @param productId
   * @return 경매 상품 상세 정보 반환
   * @author : puclpu
   */
  @Override
  @Transactional(readOnly = true)
  public ProductInfoDto getProduct(Long productId) {

    Product product = findProduct(productId);
    List<ProductImage> productImageList = findProductImageList(product);
    Duration remainingExpiration = productBidPriceMaxRepository.getRemainingExpiration(productId);
    int bidMaxPrice = productBidPriceMaxRepository.getValueByKey(productId);

    return ProductInfoDto.of(product, productImageList, remainingExpiration, bidMaxPrice);
  }

  /**
   * 경매 상품 목록 조회
   * @param memberId 상품 등록 회원 아이디
   * @param openProduct 구매 가능한 매물 선택 여부
   * @param closedProduct 마감 된 매물 선택 여부
   * @param keyword 검색어
   * @param cursorStatus 사용자에게 응답해준 마지막 데이터의 상태
   * @param cursorStartDateTime 사용자에게 응답해준 마지막 데이터의 경매 시작 시각
   * @param cursorId 사용자에게 응답해준 마지막 데이터의 식별자값
   * @param size 조회할 데이터 개수
   * @return 조회한 경매 상품 목록
   * @author : puclpu
   */
  @Override
  @Transactional(readOnly = true)
  public Slice<ProductSummaryDto> getProductSlice(
          Long memberId, boolean openProduct, boolean closedProduct, String keyword,
          ProductStatus cursorStatus, LocalDateTime cursorStartDateTime, Long cursorId, int size) {
    ProductSearchCondition productSearchCondition = ProductSearchCondition.of(memberId, openProduct, closedProduct, keyword, cursorStatus, cursorStartDateTime, cursorId);
    Pageable pageable = PageRequest.of(0, size);
    Slice<ProductSummaryDto> productSummaryDtoSlice = productRepository.findByFiltersAndSort(productSearchCondition, pageable);
    return productSummaryDtoSlice;
  }

  /**
   * 경매 상품 수정
   * @param productId 상품 아이디
   * @param requestDto 상품 수정 요청 정보
   * @param productImages 상품 이미지
   * @param memberId 등록자
   * @param now 현재 시각
   * @return 수정된 상품 정보 반환
   * @author : puclpu
   */
  @Override
  @Transactional
  public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId, LocalDateTime now) {

    // 등록된 상품인지 판별
    Product product = findProduct(productId);

    // 수정을 요청한 사용자와 판매자가 동일한지 판별
    Long writer = product.getMember().getMemberId();
    if (writer != memberId) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }

    // 입찰자 존재 여부 판별
    if (product.getBiddingCount() > 0) {
      throw CustomException.from(ExceptionCode.BIDDER_ALREADY_EXIST);
    }

    // 경매 마감일이 지났는지 판별
    LocalDateTime maxEndDateTime = product.getMaxEndDateTime();
    LocalDateTime dynamicEndDateTime = product.getDynamicEndDateTime();
    if (maxEndDateTime.isBefore(now) || dynamicEndDateTime != null && dynamicEndDateTime.isBefore(now)) {
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_CLOSED);
    }

    // 상품 이미지 변경 시 이미지 삭제 후 새로운 이미지 등록
    List<ProductImage> savedProductImages = new ArrayList<>();
    if (productImages.size() > 0) { // 이미지 변동 사항이 있으면
      // 등록된 이미지 모두 삭제
      List<ProductImage> productImageList = findProductImageList(product);
      s3Uploader.deleteProductImageFileList(productImageList);
      productImageRepository.deleteAllInBatch(productImageList);

      // 새 이미지 S3 업로드
      List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages,
          S3_IMAGE_PATH);

      // 업로드 된 상품의 url 저장
      for (String uploadedFileName : uploadedFileNames) {
        ProductImage productImage = ProductImage.of(product, uploadedFileName);
        savedProductImages.add(productImage);
      }
    }

    ProductRankingDto dto = productRepository.findRankingDtoById(productId);
    likeService.deleteLikeFromZSet(dto);

    productImageRepository.saveAll(savedProductImages);

    // 상품 정보 수정
    product.update(requestDto);

    ProductRankingDto productRankingDto = ProductRankingDto.of(product, savedProductImages.get(0));
    likeCountRankingRepository.setZSetValue("ranking", productRankingDto,
        product.getLikeCount());

    return ProductUpdateResponseDto.from(product, savedProductImages);
  }

  /**
   * 경매 상품 삭제
   * @param productId 상품 아이디
   * @param memberId 등록자
   * @param now 현재 시각
   * @author : puclpu
   */
  @Override
  @Transactional
  public void deleteProduct(Long productId, Long memberId, LocalDateTime now) {
    // 등록된 상품인지 판별
    Product product = findProduct(productId);

    // 수정을 요청한 사용자와 판매자가 동일한지 판별
    Long writer = product.getMember().getMemberId();
    if (writer != memberId) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }

    // 경매 마감일이 지났는지 판별
    LocalDateTime maxEndDateTime = product.getMaxEndDateTime();
    LocalDateTime dynamicEndDateTime = product.getDynamicEndDateTime();
    if (maxEndDateTime.isBefore(now) || dynamicEndDateTime != null && dynamicEndDateTime.isBefore(now)) {
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_CLOSED);
    }

    // 입찰자 존재 여부 판별
    if (product.getBiddingCount() > 0) {
      throw CustomException.from(ExceptionCode.BIDDER_ALREADY_EXIST); // 입찰자가 존재하면 삭제 불가
    }

    // 판매자의 보증금 환불
    Member member = findMember(memberId);
    Deposit deposit = depositRepository.findByProduct(product);
    member.addCash(deposit.getPrice());
    deposit.setStatus(DepositStatus.RETURNED);

    // 상품 이미지 삭제
    List<ProductImage> productImageList = findProductImageList(product);
    productImageRepository.deleteAllInBatch(productImageList);

    // 상품 삭제
    productRepository.delete(product);
  }

  /**
   * 경매 상품 상태 정기 업데이트
   * @param status
   * @param startDateTime
   * @author : puclpu
   */
  @Override
  @Transactional
  public void updateProductStatus(ProductStatus status, LocalDateTime startDateTime) {
    List<Product> products = productRepository.findAllByStatusAndStartDateTime(status, startDateTime);
    for (Product product : products) {
      switch (status) {
        case UPCOMING:
          product.setStatus(ProductStatus.ONGOING);
          break;
        case ONGOING:
          product.setStatus(ProductStatus.ENDED);
      }
    }
  }

  /**
   * 경매 상품 입찰자수 TOP5 조회
   * @return TOP5 상품 목록
   * @author : puclpu
   */
  @Override
  public List<ProductRankingDto> getTop5Products() {
    Set<TypedTuple<ProductRankingDto>> typedTuples = productBidderCountRankingRepository.getReverseZSetTupleByKey(
        "RANKING", 0, 4);

    List<ProductRankingDto> top5ProductsList = new ArrayList<>();
    for (TypedTuple<ProductRankingDto> tuple : typedTuples) {
      ProductRankingDto productRankingDto = jacksonObjectMapper.convertValue(tuple.getValue(), ProductRankingDto.class);
      top5ProductsList.add(productRankingDto);
    }
    return top5ProductsList;
  }

  /**
   * 경매 상품 입찰자수 TOP5 초기화
   * @author : puclpu
   */
  @Override
  public void deleteTop5Products() {
    productBidderCountRankingRepository.deleteZSetKey("RANKING");
  }

  private List<ProductImage> findProductImageList(Product product) {
    List<ProductImage> productImageList = productImageRepository.findAllByProduct(product);
    return productImageList;
  }

  private Product findProduct(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));
    return product;
  }

  private Member findMember(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
    return member;
  }
}
