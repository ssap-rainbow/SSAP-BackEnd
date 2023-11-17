package ssap.ssap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.*;
import ssap.ssap.dto.TaskRequestDto;
import ssap.ssap.repository.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final DetailedItemRepository detailedItemRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final UserRepository userRepository;
//    private final UserTestRepository userTestRepository;

    @Transactional
    public Task createPost(TaskRequestDto.CreateForm createForm) {

        User user = null;
        Category category = null;
        DetailedItem detailedItem =null;

        Optional<User> optionalUser = userRepository.findByEmail(createForm.getEmail());
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        Optional<Category> optionalCategory = categoryRepository.findByCategoryName(createForm.getCategory());
        if (optionalCategory.isPresent()) {
            category = optionalCategory.get();
            List<DetailedItem> detailedItems = detailedItemRepository.findByCategory_Id(category.getId());

            Optional<DetailedItem> optionalDetailedItem = detailedItems.stream()
                    .filter(item -> item.getDetailedItemName().equals(createForm.getDetailedItem()))
                    .findAny();

            if (optionalDetailedItem.isPresent()) {
                detailedItem = optionalDetailedItem.get();
            }
        }

        Task task = new Task();
        task.setTitle(createForm.getTitle());
        task.setDescription(createForm.getDescription());
//        task.setLocation(createForm.getLocation());
        task.setRoadAddress(createForm.getRoadAddress());
        task.setJibunAddress(createForm.getJibunAddress());
        task.setDetailedAddress(createForm.getDetailedAddress());
        task.setPreferredGender(createForm.getPreferredGender());
        if (createForm.getImmediateExecutionStatus().equals(false)) {
            task.setStartTime(createForm.getStartTime());
        }
        // ToDo: 끝나는 시간 비지니스 로직 수행 필요
        task.setEndTime(createForm.getEstimatedTime());

        task.setFee(createForm.getFee());
        task.setAuctionStatus(createForm.getAuctionStatus());
        task.setTermsAgreed(createForm.getTermsAgreed());
        task.setAuctionStartTime(createForm.getAuctionStartTime());
        task.setAuctionEndTime(createForm.getAuctionEndTime());
        if (task.getAuctionStatus().equals(true)) {
            task.setStatus("경매중");
        } else {
            task.setStatus("대기중");
        }

        TaskAttachment taskAttachment = new TaskAttachment();
        taskAttachment.setFileData(createForm.getFileData());

        //Foreign Key Mapping
        task.setUser(user);
        task.setCategory(category);
        task.setDetailedItem(detailedItem);
        taskAttachment.setTask(task);

        Task saveTask = taskRepository.save(task);
        taskAttachmentRepository.save(taskAttachment);
        return saveTask;
    }
}
