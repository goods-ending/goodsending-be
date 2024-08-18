/**
 * @Date : 2024. 08. 17.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
package com.goodsending.bid.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.goodsending.IntegrationTestSupport;
import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import com.goodsending.bid.entity.Bid;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.product.entity.Product;
import com.goodsending.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class BidRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private BidRepository bidRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ProductRepository productRepository;

  @DisplayName("productId로 상품의 입찰 수를 조회한다.")
  @Test
  void countByProductId() {
    // given
    Member member = createAndSaveMember("example1@example.com");
    Member member2 = createAndSaveMember("example2@example.com");
    Member member3 = createAndSaveMember("example3@example.com");

    Product product = createAndSaveProduct(member);
    Product product2 = createAndSaveProduct(member);

    Bid bid1 = createBid(member, product, 10000);
    Bid bid2 = createBid(member2, product, 20000);
    Bid bid3 = createBid(member3, product, 30000);
    Bid bid4 = createBid(member, product, 40000);
    Bid bid5 = createBid(member, product2, 50000);
    Bid bid6 = createBid(member2, product2, 60000);
    Bid bid7 = createBid(member, product2, 70000);
    bidRepository.saveAll(List.of(bid1, bid2, bid3, bid4, bid5, bid6, bid7));

    // when
    Long countByProduct1 = bidRepository.countByProduct(product.getId());
    Long countByProduct2 = bidRepository.countByProduct(product2.getId());

    // then
    assertThat(countByProduct1).isEqualTo(4);
    assertThat(countByProduct2).isEqualTo(3);
  }

  @DisplayName("상품별 멤버 중복을 제거한 입찰 수를 조회한다.")
  @Test
  void countDistinctMembersByProduct() {
    // given
    Member member = createAndSaveMember("example1@example.com");
    Member member2 = createAndSaveMember("example2@example.com");
    Member member3 = createAndSaveMember("example3@example.com");

    Product product = createAndSaveProduct(member);
    Product product2 = createAndSaveProduct(member);

    Bid bid1 = createBid(member, product, 10000);
    Bid bid2 = createBid(member2, product, 20000);
    Bid bid3 = createBid(member3, product, 30000);
    Bid bid4 = createBid(member, product, 40000);
    Bid bid5 = createBid(member, product2, 50000);
    Bid bid6 = createBid(member2, product2, 60000);
    Bid bid7 = createBid(member, product2, 70000);
    bidRepository.saveAll(List.of(bid1, bid2, bid3, bid4, bid5, bid6, bid7));

    // when
    Long countDistinctMembersByProduct1 = bidRepository.countDistinctMembersByProduct(
        product.getId());
    Long countDistinctMembersByProduct2 = bidRepository.countDistinctMembersByProduct(
        product2.getId());

    // then
    assertThat(countDistinctMembersByProduct1).isEqualTo(3);
    assertThat(countDistinctMembersByProduct2).isEqualTo(2);

  }

  @DisplayName("상품별 입찰 리스트를 조회한다.")
  @Test
  void findByProductId(){
    // given
    Member member = createAndSaveMember("example1@example.com");
    Member member2 = createAndSaveMember("example2@example.com");
    Member member3 = createAndSaveMember("example3@example.com");

    Product product = createAndSaveProduct(member);
    Product product2 = createAndSaveProduct(member);

    Bid bid1 = createBid(member, product, 10000);
    Bid bid2 = createBid(member2, product, 20000);
    Bid bid3 = createBid(member3, product, 30000);
    Bid bid4 = createBid(member, product, 40000);
    Bid bid5 = createBid(member, product2, 50000);
    Bid bid6 = createBid(member2, product2, 60000);
    Bid bid7 = createBid(member, product2, 70000);
    bidRepository.saveAll(List.of(bid1, bid2, bid3, bid4, bid5, bid6, bid7));

    // when
    List<Bid> bids1 = bidRepository.findByProductId(product.getId());
    List<Bid> bids2 = bidRepository.findByProductId(product2.getId());

    // then
    assertThat(bids1).hasSize(4)
            .extracting("member", "product", "price")
                .containsExactlyInAnyOrder(
                    tuple(member, product, 10000),
                    tuple(member2, product, 20000),
                    tuple(member3, product, 30000),
                    tuple(member, product, 40000));

    assertThat(bids2).hasSize(3)
        .extracting("member", "product", "price")
        .containsExactlyInAnyOrder(
            tuple(member, product2, 50000),
            tuple(member2, product2, 60000),
            tuple(member, product2, 70000));
  }

  @DisplayName("BidListByMemberRequest("
      + "Long loginMemberId,Long memberId,Long cursorId,int pageSize)"
      + "상품별 입찰 리스트를 조회한다.")
  @Test
  void findBidWithProductResponseList(){
    // given
    Member member = createAndSaveMember("example1@example.com");
    Member member2 = createAndSaveMember("example2@example.com");
    Member member3 = createAndSaveMember("example3@example.com");

    Product product = createAndSaveProduct(member);
    Product product2 = createAndSaveProduct(member);

    Bid bid1 = createBid(member, product, 10000);
    Bid bid2 = createBid(member2, product, 20000);
    Bid bid3 = createBid(member3, product, 30000);
    Bid bid4 = createBid(member, product, 40000);
    Bid bid5 = createBid(member, product, 50000);
    Bid bid6 = createBid(member, product2, 55000);
    Bid bid7 = createBid(member2, product2, 60000);
    Bid bid8 = createBid(member, product2, 70000);
    Bid bid9 = createBid(member, product2, 80000);
    Bid bid10 = createBid(member, product2, 90000);

    bidRepository.saveAll(List.of(bid1, bid2, bid3, bid4, bid5, bid6, bid7, bid8, bid9, bid10));
    Long cursorId = null;
    int pageSize = 3;
    BidListByMemberRequest bidListByMemberRequest = new BidListByMemberRequest(member.getMemberId(),
        member.getMemberId(), cursorId, pageSize);
    // when
    Slice<BidWithProductResponse> bids1 = bidRepository.findBidWithProductResponseList(
        bidListByMemberRequest);

    // then
    assertThat(bids1).hasSize(3)
        .extracting("memberId", "productSummaryDto.productId", "bidPrice")
        .containsExactlyInAnyOrder(
            tuple(member.getMemberId(), product2.getId(), 70000),
            tuple(member.getMemberId(), product2.getId(), 80000),
            tuple(member.getMemberId(), product2.getId(), 90000));
  }

  private Member createAndSaveMember(String email) {
    return memberRepository.save(createMember(email));
  }

  private Product createAndSaveProduct(Member member) {
    return productRepository.save(createProduct(member));
  }

  private Member createMember(String email) {
    return Member.builder()
        .email(email)
        .password("1234")
        .role(MemberRole.USER)
        .build();
  }

  private Product createProduct(Member member) {
    return Product.builder()
        .name("상품이름")
        .price(10000)
        .introduction("상품소개")
        .startDateTime(LocalDateTime.now())
        .startDateTime(LocalDateTime.now().plusHours(3))
        .member(member)
        .build();
  }

  private Bid createBid(Member member, Product product, Integer price) {
    return Bid.builder()
        .price(price)
        .member(member)
        .product(product)
        .build();
  }
}