package com.goodsending.product.repository;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.type.ProductStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(LocalDateTime currentDateTime);

  Slice<ProductSummaryDto> findByFiltersAndSort(Long memberId, boolean openProduct, boolean closedProduct, String keyword,
      ProductStatus cursorStatus, LocalDateTime cursorStartDateTime, Long cursorId, Pageable pageable);

  List<Product> findAllByStatusAndStartDateTime(ProductStatus status, LocalDateTime startDateTime);

}
