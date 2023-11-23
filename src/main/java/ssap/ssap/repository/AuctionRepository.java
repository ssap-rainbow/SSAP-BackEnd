package ssap.ssap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssap.ssap.domain.Auction;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
