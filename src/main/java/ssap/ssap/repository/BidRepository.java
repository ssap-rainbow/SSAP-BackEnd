package ssap.ssap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssap.ssap.domain.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT MIN(b.amount) FROM Bid b WHERE b.auction.id = :auctionId")
    Integer findLowestBidAmountByAuctionId(@Param("auctionId") Long auctionId);

    Bid findTopByAuctionIdOrderByTimeDesc(Long auctionId);
}
