package ssap.ssap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ssap.ssap.domain.*;
import ssap.ssap.dto.TaskRequestDto;
import ssap.ssap.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final DetailedItemRepository detailedItemRepository;
    private final ThumbNailRepository thumbNailRepository; // 수정됨
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client; // S3Client 주입

//    private final UserTestRepository userTestRepository;


    @Value("${AWS_S3_BUCKET:defaultS3Bucket}")
    private String bucketName;

    @Value("${AWS_REGION:us-west-2}")
    private String region;


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

        // Task 엔티티 생성 및 필드 설정
        Task task = new Task();
        task.setTitle(createForm.getTitle());
        task.setDescription(createForm.getDescription());
        task.setRoadAddress(createForm.getRoadAddress());
        task.setJibunAddress(createForm.getJibunAddress());
        task.setDetailedAddress(createForm.getDetailedAddress());
        task.setPreferredGender(createForm.getPreferredGender());
        if (!createForm.getImmediateExecutionStatus()) {
            task.setStartTime(createForm.getStartTime());
        }
        task.setEndTime(createForm.getEstimatedTime());
        task.setFee(createForm.getFee());
        task.setAuctionStatus(createForm.getAuctionStatus());
        task.setTermsAgreed(createForm.getTermsAgreed());
        task.setAuctionStartTime(createForm.getAuctionStartTime());
        task.setAuctionEndTime(createForm.getAuctionEndTime());
        task.setStatus(createForm.getAuctionStatus() ? "경매중" : "대기중");

        taskRepository.save(task); // task 저장

        // 파일 업로드 처리
        if (createForm.getFiles() != null && !createForm.getFiles().isEmpty()) {
            List<ThumbNailEntity> thumbNails = uploadFilesToS3(createForm.getFiles());
            task.setThumbNails(thumbNails); // Task에 ThumbNailEntity 설정
            for (ThumbNailEntity thumbNail : thumbNails) {
                thumbNail.setTask(task); // ThumbNailEntity에 Task 연결
                thumbNailRepository.save(thumbNail);
            }
        }

        // 외래 키 매핑
        task.setUser(user);
        task.setCategory(category);
        task.setDetailedItem(detailedItem);

        return task;
    }
    private List<ThumbNailEntity> uploadFilesToS3(List<MultipartFile> files) {
        List<ThumbNailEntity> thumbNails = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = uploadFileToS3(file); // 각 파일을 S3에 업로드하고 URL을 받아옵니다.
            ThumbNailEntity thumbNail = new ThumbNailEntity();
            thumbNail.setFileData(fileUrl); // 업로드된 파일 URL 설정
            thumbNails.add(thumbNail);
        }
        return thumbNails;
    }

    private String uploadFileToS3(MultipartFile file) {
        try {
            String fileName = generateFileName(file); // 파일 이름 생성
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName; // 업로드된 파일 URL 반환
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException(e);
        }
    }

    // 파일 이름 생성 로직 (예: UUID 사용)
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }
}
