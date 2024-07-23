package com.goodsending.product.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductImageInfoDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final S3Uploader s3Uploader;

  @Override
  @Transactional
  public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId) {

    // 존재하는 회원인지 판별
    if (memberId == null) {
      throw CustomException.of(ExceptionCode.USER_NOT_FOUND);
    }

    // 상품 정보 저장
    Product product = Product.of(requestDto, memberId);
    Product savedProduct = productRepository.save(product);

    // 상품 이미지 업로드
    List<ProductImageInfoDto> savedProductImages = new ArrayList<>();
    for (MultipartFile productImageFile : productImages) {
      try {
        // S3 업로드
        String storedFileName = s3Uploader.upload(productImageFile, "images/products");

        // 업로드 된 url 저장
        ProductImage productImage = ProductImage.of(product, storedFileName);
        productImageRepository.save(productImage);

        ProductImageInfoDto productImageInfoDto = ProductImageInfoDto.from(productImage);
        savedProductImages.add(productImageInfoDto);
      } catch (MaxUploadSizeExceededException e) { // 파일 용량 초과
        throw CustomException.of(ExceptionCode.FILE_SIZE_EXCEEDED);
      } catch (IOException e) {
        if (e.getMessage().contains("No space left on device")) {
          throw CustomException.of(ExceptionCode.LOW_DISK_SPACE); // 디스크 공간 부족
        } else {
          throw CustomException.of(ExceptionCode.FILE_UPLOAD_FAILED); // 파일 업로드 실패
        }
      }
    }

    return ProductCreateResponseDto.of(product, savedProductImages);
  }
}
