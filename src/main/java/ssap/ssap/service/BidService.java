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
        Integer taskFee = Integer.valueOf(task.getFee());

        if (currentLowestBid != null) {
            if (bidRequest.getBidAmount() >= currentLowestBid) {
                log.error("입찰 실패: 입찰 금액 ({})은 현재 최저 입찰 금액 ({})보다 낮아야 함", bidRequest.getBidAmount(), currentLowestBid);
                throw new IllegalArgumentException("입찰 금액은 현재 최저 입찰 금액보다 낮아야 합니다.");
            }
        } else if (bidRequest.getBidAmount() >= taskFee) {
            // 최초 입찰인 경우, 입찰 금액이 Task의 fee보다 낮아야 함
            log.error("입찰 실패: 입찰 금액 ({})은 Task의 fee 금액 ({})보다 낮아야 함", bidRequest.getBidAmount(), taskFee);
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
        log.debug("경매 ID에 따른 최신 입찰 찾기: 값 = {}, 타입 = {}", auctionId, auctionId.getClass().getSimpleName());
        Bid latestBid = bidRepository.findTopByAuctionIdOrderByTimeDesc(auctionId);

        if (latestBid != null) {
            return new BidResponseDto(
                    latestBid.getId(),
                    latestBid.getAmount(),
                    latestBid.getUser().getEmail(),
                    latestBid.getUser().getName(),
                    latestBid.getTime()
            );
        } else {
            // latestBid가 null인 경우, 관련 Auction을 찾아서 해당 Task의 fee 값을 리턴
            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new EntityNotFoundException("경매를 찾을 수 없습니다: " + auctionId));
            Task task = auction.getTask();
            Integer fee = Integer.valueOf(task.getFee());

            return new BidResponseDto(
                    null,
                    fee,
                    null,
                    null,
                    null
            );
        }
    }
}
