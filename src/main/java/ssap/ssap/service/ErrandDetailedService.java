package ssap.ssap.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssap.ssap.domain.Task;
import ssap.ssap.domain.User;
import ssap.ssap.repository.TaskRepository;
import ssap.ssap.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class ErrandDetailedService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public ErrandDetailedService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // taskId를 받아 심부름 상세 정보를 조회
    public Map<String, Object> getErrandDetails(Long taskId) {
        // Task 객체를 찾으며, 찾을 수 없다면 예외를 발생
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("taskId 을 찾을 수 없습니다.: " + taskId));

        // User 객체를 찾으며, 찾을 수 없다면 예외를 발생
        User requester = userRepository.findById(task.getUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("userId 을 찾을 수 없습니다.: " + task.getUser().getUserId()));

        // 조회된 정보를 바탕으로 Map을 생성하여 반환
        Map<String, Object> details = new HashMap<>();

        // 각 필드에 대한 정보를 Map에 저장
        details.put("categoryTag", task.getCategory().getCategoryName());
        details.put("detailitem", task.getDetailedItem().getDetailedItemName());
        details.put("title", task.getTitle());
        details.put("description", task.getDescription());
        details.put("fee", task.getFee());
        details.put("auctionStartTime", task.getAuctionStartTime());
        details.put("auctionEndTime", task.getAuctionEndTime());
        details.put("StartTime", task.getStartTime());
        details.put("EndTime", task.getEndTime());
        details.put("roadAddress", task.getRoadAddress());
        details.put("jibunAddress", task.getJibunAddress());
        details.put("detailedAddress", task.getDetailedAddress());
        details.put("name", task.getUser().getName());
        details.put("ageRange", task.getUser().getAgeRange());
        details.put("gender", task.getUser().getGender());
        details.put("profileImageUrl", task.getUser().getProfileImageUrl());
        details.put("auctionId",task.getAuction().getId());

        return details;
    }
}
