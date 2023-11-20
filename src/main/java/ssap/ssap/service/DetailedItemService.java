package ssap.ssap.service;

import org.springframework.stereotype.Service;
import ssap.ssap.domain.DetailedItem;
import ssap.ssap.dto.DetailedItemDTO;
import ssap.ssap.repository.DetailedItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailedItemService {

    private final DetailedItemRepository detailItemRepository;

    public DetailedItemService(DetailedItemRepository detailItemRepository) {
        this.detailItemRepository = detailItemRepository;
    }

    public List<DetailedItemDTO> findAllDetailItemsByCategoryId(Long categoryId) {
        List<DetailedItem> detailItems = detailItemRepository.findByCategory_Id(categoryId);
        return detailItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DetailedItemDTO convertToDTO(DetailedItem detailItem) {
        return new DetailedItemDTO(detailItem.getId(), detailItem.getDetailedItemName());
    }
}

