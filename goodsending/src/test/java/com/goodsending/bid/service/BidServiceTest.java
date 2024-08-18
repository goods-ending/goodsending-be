package com.goodsending.bid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.goodsending.IntegrationTestSupport;
import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.repository.BidRepository;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.global.exception.CustomException;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.product.entity.Product;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.productmessage.repository.ProductMessageHistoryRepository;
import com.goodsending.productmessage.type.MessageType;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class BidServiceTest extends IntegrationTestSupport {

  @Autowired
  private BidService bidService;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductMessageHistoryRepository productMessageHistoryRepository;

  @Autowired
  private ProductBidPriceMaxRepository productBidPriceMaxRepository;

  @Autowired
  private RedisTemplate<String, Integer> redisTemplate;

  @AfterEach
  void tearDown(){
    bidRepository.deleteAllInBatch();
    productMessageHistoryRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    Set<String> keys = redisTemplate.keys("*"); // 모든 키를 가져옴
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys); // 키들 삭제
    }
  }

  @DisplayName("상품에 사용자의 캐시와 포인트를 사용하여 입찰을 진행한다.")
  @Test
  void create(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);

    // when
    BidResponse bidResponse = bidService.create(bidder.getMemberId(),
        new BidRequest(11000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)));

    // then
    assertThat(bidResponse)
        .extracting("price", "usePoint", "memberId", "productId", "biddingCount",
            "bidderCount")
        .contains(11000, 500, bidder.getMemberId(), product1.getId(), 1, 1);

    assertThat(bidRepository.findAll())
        .hasSize(1)
        .extracting("price", "usePoint", "status", "member.memberId", "product.id")
        .containsExactlyInAnyOrder(
            tuple(11000, 500, null, bidder.getMemberId(), product1.getId()));

    Member bidderResult = memberRepository.findById(bidder.getMemberId())
        .orElseThrow(() -> new RuntimeException());
    assertThat(bidderResult)
        .extracting("cash", "point")
        .contains(0, 0);

    Product productResult = productRepository.findById(product1.getId())
        .orElseThrow(() -> new RuntimeException());
    assertThat(productResult)
        .extracting("biddingCount", "bidderCount")
        .contains(1, 1);

    assertThat(productMessageHistoryRepository.findAll())
        .hasSize(1)
        .extracting("member.memberId", "product.id", "message", "type")
        .containsExactlyInAnyOrder(
            tuple(bidder.getMemberId(), product1.getId(),
                "ex******@example.com님이 11,000원에 입찰하셨습니다.\n", MessageType.BID)
        );
  }

  @DisplayName("이미 낙찰된 경매는 입찰을 진행할 수 없다.")
  @Test
  void createWithAuctionAlreadyWon(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(13, 1, 1)));

    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(11000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(14, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("이미 낙찰된 경매입니다.");
  }

  @DisplayName("아직 시작되지 않은 경매는 입찰을 진행할 수 없다.")
  @Test
  void createWithAuctionNotStarted(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);

    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(11000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(11, 59, 59))))
        .isInstanceOf(CustomException.class)
        .hasMessage("경매가 아직 시작되지 않았습니다.");

  }

  @DisplayName("이미 마감된 경매는 입찰을 진행할 수 없다.")
  @Test
  void createWithAuctionAlreadyClosed(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);

    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(11000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 1))))
        .isInstanceOf(CustomException.class)
        .hasMessage("이미 마감된 경매입니다.");

  }

  @DisplayName("현재 최고 입찰 금액이 입력한 금액보다 같거나 큰 경우는 입찰을 진행할 수 없다.")
  @Test
  void createWithBidAmountLessThanCurrentMax(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    productBidPriceMaxRepository.setValue(product1.getId(), 11000, Duration.ofMinutes(5));
    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(11000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("현재 최고 입찰 금액이 입력한 금액보다 큽니다.");

  }

  @DisplayName("경매 시작 금액이 입력한 금액보다 클 경우는 입찰을 진행할 수 없다.")
  @Test
  void createWithInsufficientBidAmount(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(10000, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("경매 기본가가 입력한 금액보다 큽니다.");
  }

  @DisplayName("회원 정보가 없는 회원은 입찰을 진행할 수 없다.")
  @Test
  void createWithUserNotFound(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    // when
    // then
    assertThatThrownBy(() -> bidService.create(0L,
        new BidRequest(10001, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("유저 개체를 찾지 못했습니다.");
  }

  @DisplayName("사용하려는 포인트가 신청 입찰가를 초과할 수는 없다.")
  @Test
  void createWithExcessivePoint(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 10002);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(10001, 10002, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("포인트가 신청 입찰금을 초과합니다.");

  }

  @DisplayName("유저의 캐시가 충분하지 않으면 입찰을 진행할 수 없다.")
  @Test
  void createWithInsufficientUserCash(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 9500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(10001, 500, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("입력한 금액이 유저 캐시보다 큽니다.");
  }

  @DisplayName("유저의 포인트가 충분하지 않으면 입찰을 진행할 수 없다.")
  @Test
  void createWithInsufficientUserPoint(){
    // given
    Member seller = createAndSaveMember("example1@example.com", 0, 0);
    Member bidder = createAndSaveMember("example2@example.com", 10500, 500);

    LocalDate date = LocalDate.of(2024, 5, 2);
    Product product1 = createAndSaveProduct(seller,
        10000,
        LocalDateTime.of(date, LocalTime.of(12, 0, 0)),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0)),
        null);
    // when
    // then
    assertThatThrownBy(() -> bidService.create(bidder.getMemberId(),
        new BidRequest(10001, 501, product1.getId()),
        LocalDateTime.of(date, LocalTime.of(15, 0, 0))))
        .isInstanceOf(CustomException.class)
        .hasMessage("입력한 금액이 유저 포인트보다 큽니다.");
  }

  private Member createAndSaveMember(String email, Integer cash, Integer point) {
    return memberRepository.save(createMember(email, cash, point));
  }

  private Product createAndSaveProduct(Member member,
      int price,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      LocalDateTime dynamicEndDateTime) {
    return productRepository.save(createProduct(member, price, startDateTime, endDateTime, dynamicEndDateTime));
  }

  private Member createMember(String email, Integer cash, Integer point) {
    Member member = Member.builder()
        .email(email)
        .password("1234")
        .role(MemberRole.USER)
        .build();
    ReflectionTestUtils.setField(member, "cash", cash);
    ReflectionTestUtils.setField(member, "point", point);
    return member;
  }

  private Product createProduct(Member member,
      int price,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      LocalDateTime dynamicEndDateTime) {
    Product product = Product.builder()
        .name("상품이름")
        .price(price)
        .introduction("상품소개")
        .startDateTime(startDateTime)
        .maxEndDateTime(endDateTime)
        .member(member)
        .build();
    ReflectionTestUtils.setField(product, "dynamicEndDateTime", dynamicEndDateTime);
    return product;
  }

}