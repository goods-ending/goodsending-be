package com.goodsending.productlike.service;

import com.goodsending.product.dto.response.ProductRankingDto;
import com.goodsending.product.dto.response.ProductlikeCountDto;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface LikeService{
  ResponseEntity<LikeResponseDto> toggleLike(Long memberId, LikeRequestDto likeRequestDto);

  Page<ProductlikeCountDto> getLikeProductsPage(Long memberId, int page, int size,
      String sortBy, boolean isAsc);

  List<ProductRankingDto> getTop5LikeProduct(LocalDateTime dateTime);

  ResponseEntity<LikeResponseDto> toggleLikeRedis(Long memberId, LikeRequestDto requestDto);

  List<ProductRankingDto> readTop5LikeProduct();

  ProductRankingDto convertMapToDto(Map<String, Object> map);

  void resetTop5Likes(LocalDateTime startDateTime);

  void deleteLikeFromZSet(ProductRankingDto rankingDto);
}
