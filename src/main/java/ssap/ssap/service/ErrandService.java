package ssap.ssap.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.ErrandEntity;
import ssap.ssap.dto.ErrandDTO;
import ssap.ssap.repository.ErrandRepository;
import ssap.ssap.repository.ThumbNailRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErrandService {
    private final ErrandRepository errandRepository;
    private final ThumbNailRepository thumbNailRepository;
    public ErrandService(ErrandRepository errandRepository, ThumbNailRepository thumbNailRepository) {
        this.errandRepository = errandRepository;
        this.thumbNailRepository = thumbNailRepository;
    }
    public List<ErrandDTO> findAllErrands() {
        List<ErrandEntity> errands = errandRepository.findAll();
        return errands.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private ErrandDTO convertToDTO(ErrandEntity errand) {
        ErrandDTO dto = new ErrandDTO();
        dto.setTaskId(errand.getId());
        dto.setTitle(errand.getTitle());
        dto.setDescription(errand.getDescription());
        dto.setCreationTime(errand.getAuctionStartTime());
        dto.setAuctionEndTime(errand.getAuctionEndTime());
        if (errand.getAttachment() != null) {
            dto.setThumbnailUrl(errand.getAttachment().getFileData());
        }
        return dto;
    }
    @Transactional(readOnly = true)
    public Page<ErrandDTO> findAllErrands(Pageable pageable) {
        return errandRepository.findAll(pageable)
                .map(this::convertToDTO); // 페이지를 DTO로 변환합니다.
    }
    // 기타 CRUD 메소드 작성해야함. 현재는 조회만 가능.
}
