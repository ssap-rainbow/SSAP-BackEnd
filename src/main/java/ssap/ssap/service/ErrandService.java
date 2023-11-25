package ssap.ssap.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssap.ssap.domain.Task;
import ssap.ssap.domain.TaskAttachment;
import ssap.ssap.dto.ErrandResponseDto;
import ssap.ssap.repository.TaskAttachmentRepository;
import ssap.ssap.repository.TaskRepository;

@Service
@Slf4j
public class ErrandService {
    private final TaskRepository taskRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    public ErrandService(TaskRepository taskRepository, TaskAttachmentRepository taskAttachmentRepository) {
        this.taskRepository = taskRepository;
        this.taskAttachmentRepository = taskAttachmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<ErrandResponseDto> getErrands(String address, Pageable pageable) {
        if (address == null || address.isEmpty()) {
            // 주소 정보가 없는 경우, 전체 심부름 목록을 반환
            return findAllErrands(pageable);
        } else {
            // 주소 정보가 있는 경우, 위치 기반으로 필터링된 심부름 목록을 반환
            return getFilteredErrands(address, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<ErrandResponseDto> findAllErrands(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    private ErrandResponseDto convertToDto(Task task) {
        ErrandResponseDto dto = new ErrandResponseDto();
        dto.setTaskId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setFee(task.getFee());
        dto.setStartTime(task.getStartTime());
        dto.setAuctionEndTime(task.getAuctionEndTime());
        setFirstAttachmentFileData(task, dto);
        // '동'을 추출하는 로직
        String district = extractDistrictFromAddress(task.getJibunAddress());
        dto.setDistrict(district);

        return dto;
    }

    private void setFirstAttachmentFileData(Task task, ErrandResponseDto dto) {
        if (!task.getAttachments().isEmpty()) {
            TaskAttachment firstAttachment = task.getAttachments().get(0);
            dto.setFileData(firstAttachment.getFileData());
        }
    }
    @Transactional(readOnly = true)
    public Page<ErrandResponseDto> getFilteredErrands(String address, Pageable pageable) {
        String district = extractDistrictFromAddress(address);
        if (district == null) {
            return Page.empty(); // '동'을 찾지 못한 경우 빈 페이지 반환
        }

        Page<Task> taskPage = taskRepository.findByJibunAddressContaining(district, pageable);
        return taskPage.map(this::convertToDto);
    }
    // 주소에서 '동'을 추출하는 메소드
//    public String extractDistrictFromAddress(String fullAddress) {
//        if(fullAddress == null || fullAddress.isEmpty()) {
//            return null;
//        }
//        String[] parts = fullAddress.split(" ");
//        for (int i = parts.length - 1; i >= 0; i--) {
//            if (parts[i].matches("\\d+.*")) {
//                return i > 0 ? parts[i - 1] : null;
//            }
//        }
//        return null; // '동'을 찾지 못한 경우
//    }

    public String extractDistrictFromAddress(String fullAddress) {
        if(fullAddress == null || fullAddress.isEmpty()) {
            return null;
        }

        // 주소를 공백으로 분할
        String[] parts = fullAddress.split(" ");

        // '구'를 포함하는 부분 찾기
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }

        return null; // '구'를 찾지 못한 경우
    }

    // 기타 CRUD 메소드 작성해야함. 현재는 조회만 가능.
}
