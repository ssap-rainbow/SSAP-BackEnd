package ssap.ssap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.*;
import ssap.ssap.dto.ErrandListResponseDto;
import ssap.ssap.repository.AuctionRepository;
import ssap.ssap.repository.TaskRepository;
import ssap.ssap.repository.UserRepository;

@Service
public class ErrandListService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public ErrandListService(TaskRepository taskRepository, UserRepository userRepository, AuctionRepository auctionRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional(readOnly = true)
    public Page<ErrandListResponseDto> getErrands(Long categoryId, String address, Pageable pageable) {
        if (address != null && !address.isEmpty()) {
            String district = extractDistrictFromAddress(address);
            if (district != null) {
                return taskRepository.findByCategoryIdAndJibunAddressContaining(categoryId, district, pageable)
                        .map(this::convertToDto);
            }
        }
        return categoryId != null
                ? taskRepository.findByCategoryId(categoryId, pageable).map(this::convertToDto)
                : taskRepository.findAll(pageable).map(this::convertToDto);
    }

    private ErrandListResponseDto convertToDto(Task task) {
        ErrandListResponseDto dto = new ErrandListResponseDto();

        dto.setTaskId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription()); // Task 객체에서 설명을 가져옵니다.
        dto.setFee(task.getFee());
        dto.setStartTime(task.getStartTime());
        dto.setAuctionEndTime(task.getAuctionEndTime());
        setFirstAttachmentFileData(task, dto);

        User user = task.getUser();
        if (task.getUser() != null) {
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getName());
        }

        Category category = task.getCategory();
        if (task.getCategory() != null) {
            dto.setCategoryId(category.getId());
            dto.setCategoryName(category.getCategoryName());
        }

        Auction auction = task.getAuction();
        if (task.getAuction() != null) {
            dto.setAuctionId(auction.getId());
        }

        if (!task.getAttachments().isEmpty()) {
            TaskAttachment firstAttachment = task.getAttachments().get(0);
            dto.setFileData(firstAttachment.getFileData());
        }

        String district = extractDistrictFromAddress(task.getJibunAddress());
        dto.setDistrict(district);

        return dto;
    }

    private void setFirstAttachmentFileData(Task task, ErrandListResponseDto dto) {
        if (!task.getAttachments().isEmpty()) {
            TaskAttachment firstAttachment = task.getAttachments().get(0);
            dto.setFileData(firstAttachment.getFileData());
        }
    }

    // 주소에서 '구'를 추출하는 메서드
    private String extractDistrictFromAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return null;
        }
        String[] parts = fullAddress.split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }
        return null; // '구'를 찾지 못한 경우
    }
}
