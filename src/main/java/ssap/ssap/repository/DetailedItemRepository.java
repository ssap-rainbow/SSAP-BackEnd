package ssap.ssap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssap.ssap.domain.DetailedItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailedItemRepository extends JpaRepository<DetailedItem, Long> {
//    Optional<DetailedItem> findByDetailedItemName(String detailedItemName);
    List<DetailedItem> findByCategory_Id(Long categoryId);
}
