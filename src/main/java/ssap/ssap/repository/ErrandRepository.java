package ssap.ssap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssap.ssap.domain.ErrandEntity;

public interface ErrandRepository extends JpaRepository<ErrandEntity, Long> {
}
