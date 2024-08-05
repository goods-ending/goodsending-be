package com.goodsending.product.repository;

import com.goodsending.product.dto.response.MyProductSummaryDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductWithSellingPriceDto;
import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(LocalDateTime currentDateTime);

  Slice<ProductSummaryDto> findByFiltersAndSort(LocalDateTime now, String openProduct, String closedProduct, String keyword,
      LocalDateTime cursorStartDateTime, Long cursorId, Pageable pageable);

  Slice<MyProductSummaryDto> findProductByMember(Long memberId, Pageable pageable, Long cursorId);

  Optional<ProductWithSellingPriceDto> findProductWithSellingPriceByProductId(Long productId);
}
