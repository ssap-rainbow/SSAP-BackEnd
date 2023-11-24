package ssap.ssap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.*;
import ssap.ssap.repository.AuctionRepository;
import ssap.ssap.repository.TaskRepository;
import ssap.ssap.repository.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> getErrandsByCategory(Long categoryId) {
        List<Task> tasks = taskRepository.findByCategoryId(categoryId);
        return tasks.stream().map(this::convertTaskToMap).collect(Collectors.toList());
    }

    private Map<String, Object> convertTaskToMap(Task task) {
        Map<String, Object> errandMap = new HashMap<>();

        errandMap.put("taskId", task.getId());
        errandMap.put("title", task.getTitle());
        errandMap.put("fee", task.getFee());
        errandMap.put("startTime", task.getStartTime());
        errandMap.put("auctionEndTime", task.getAuctionEndTime());
        errandMap.put("roadAddress", task.getRoadAddress());
        errandMap.put("jibunAddress", task.getJibunAddress());
        errandMap.put("detailedAddress", task.getDetailedAddress());

        // User 정보
        User user = task.getUser();
        if (user != null) {
            errandMap.put("userId", user.getUserId());
        }

        // Category 정보
        Category category = task.getCategory();
        if (category != null) {
            errandMap.put("categoryId", category.getId());
            errandMap.put("categoryName", category.getCategoryName());
        }

        // Auction 정보
        Auction auction = task.getAuction();
        if (auction != null) {
            errandMap.put("auctionId", auction.getId());
        }

        // TaskAttachment 정보 (첨부 파일 정보)
        List<TaskAttachment> attachments = task.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            // 첫 번째 첨부 파일 정보만 불러오기
            TaskAttachment firstAttachment = attachments.get(0);
            errandMap.put("firstAttachmentId", firstAttachment.getId());
            errandMap.put("firstAttachmentFileData", firstAttachment.getFileData());
        }
        return errandMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFilteredErrands(String address) {
        String district = extractDistrictFromAddress(address);
        if (district == null) {
            return Collections.emptyList(); // '동'을 찾지 못한 경우 빈 리스트 반환
        }
        List<Task> tasks = taskRepository.findByJibunAddressContaining(district);
        return tasks.stream().map(this::convertTaskToMap).collect(Collectors.toList());
    }

    // 주소에서 '동'을 추출하는 메서드
    public String extractDistrictFromAddress(String fullAddress) {
        if(fullAddress == null || fullAddress.isEmpty()) {
            return null;
        }
        String[] parts = fullAddress.split(" ");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i].matches("\\d+.*")) {
                return i > 0 ? parts[i - 1] : null;
            }
        }
        return null; // '동'을 찾지 못한 경우
    }

}
