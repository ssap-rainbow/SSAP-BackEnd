package ssap.ssap.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.Auction;
import ssap.ssap.domain.Bid;
import ssap.ssap.domain.Task;
import ssap.ssap.domain.User;
import ssap.ssap.dto.BidRequestDto;
import ssap.ssap.dto.BidResponseDto;
import ssap.ssap.repository.AuctionRepository;
import ssap.ssap.repository.BidRepository;
import ssap.ssap.repository.TaskRepository;
import ssap.ssap.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class BidService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public BidService(TaskRepository taskRepository, UserRepository userRepository, BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public String placeBid(BidRequestDto bidRequest) {
        log.info("입찰 요청 처리 시작: {}", bidRequest);

        Task task = taskRepository.findById(bidRequest.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("심부름을 찾을 수 없습니다: " + bidRequest.getTaskId()));
        log.debug("심부름 조회 성공: {}", task);

        User user = userRepository.findByEmail(bidRequest.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + bidRequest.getUserEmail()));
        log.debug("사용자 조회 성공: {}", user);

        // 경매 상태 및 입찰 가능 여부 검증
        Auction auction = auctionRepository.findById(bidRequest.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("경매를 찾을 수 없습니다: " + bidRequest.getAuctionId()));
        log.debug("경매 조회 성공: {}", auction);

        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new IllegalArgumentException("이미 종료된 경매에는 입찰할 수 없습니다.");
        }

        // 최저 입찰 금액 검증
        Integer currentLowestBid = bidRepository.findLowestBidAmountByAuctionId(auction.getId());
        if (currentLowestBid != null && bidRequest.getBidAmount() >= currentLowestBid) {
            throw new IllegalArgumentException("입찰 금액은 현재 최저 입찰 금액보다 낮아야 합니다.");
        }

        // 입찰 처리
        Bid newBid = new Bid();
        newBid.setTask(task);
        newBid.setUser(user);
        newBid.setAmount(bidRequest.getBidAmount());
        newBid.setTime(LocalDateTime.now());
        newBid.setTermsAgreed(bidRequest.isTermsAgreed());
        newBid.setAuction(auction);

        bidRepository.save(newBid);
        log.info("입찰 정보 저장 완료: {}", newBid);

        return "입찰이 성공적으로 완료되었습니다";
    }

    public BidResponseDto findLatestBidByAuctionId(Long auctionId) {
        Bid latestBid = bidRepository.findTopByAuctionIdOrderByTimeDesc(auctionId);
        if (latestBid == null) {
            return null;
        }

        return new BidResponseDto(
                latestBid.getId(),
                latestBid.getAmount(),
                latestBid.getUser().getEmail(),
                latestBid.getUser().getName(),
                latestBid.getTime()
        );
    }
}
