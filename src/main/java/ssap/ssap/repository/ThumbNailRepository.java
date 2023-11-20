package ssap.ssap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssap.ssap.domain.ThumbNailEntity;

public interface ThumbNailRepository extends JpaRepository<ThumbNailEntity, Long> {
}
