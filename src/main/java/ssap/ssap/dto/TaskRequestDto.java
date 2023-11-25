package ssap.ssap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ssap.ssap.domain.Task;

import java.util.List;

@Schema(description = "심부름 요청성 작성 API 호출 DTO")
public class TaskRequestDto {

    /*심부름 요청서 작성시 쓰는 DTO*/
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateForm {

        @Schema(description = "email", example = "honggildong@gmail.com")
        @NotBlank
        private String email;

        @Schema(description = "심부름 항목", example = "배달")
        @NotBlank
        private String category;

        @Schema(description = "심부름 상세 항목", example = "세탁물 배달")
        @NotBlank
        private String detailedItem;

        @Schema(description = "심부름 게시글 제목", example = "OO대학교 도서관에 책 반납하실 분 구합니다.")
        @NotBlank
        private String title;

        @Schema(description = "심부름 상세 요청 내용", example = "도서관 1층에서 해당 책 반납 부탁드립니다.")
        @NotBlank
        private String description;

        @Schema(description = "심부름을 수행할 장소의 도로명 주소", example = "서울특별시 동작구 상도로 369")
        @NotBlank
        private String roadAddress;

        @Schema(description = "심부름을 수행할 장소의 지번 주소", example = "서울특별시 동작구 상도동 511")
        @NotBlank
        private String jibunAddress;

        @Schema(description = "상세 주소", example = "중앙도서관")
        private String detailedAddress;

        @Schema(description = "선호 성별", example = "없음")
        @NotBlank
        private String preferredGender;

        @Schema(description = "심부름 즉시 수행 여부", example = "false")
        @NotNull
        private Boolean immediateExecutionStatus;

        @Schema(description = "심부름 시작 시간", example = "2023-11-14 15:00:00", defaultValue = "지금 즉시")
        private String startTime;

        @Schema(description = "심부름 종료 시간", example = "2023-11-14 16:00:00")
        private String endTime;

        @Schema(description = "심부름을 수행하기 위한 비용", example = "5000")
        @NotBlank
        private String fee;

        @Schema(description = "경매 진행 여부", example = "true")
        @NotNull
        private Boolean auctionStatus;

        @Schema(description = "약관동의여부", example = "true")
        @NotNull
        private Boolean termsAgreed;

        @Schema(description = "경매의 시작 시간", example = "2023-11-12 13:00:00")
        private String auctionStartTime;

        @Schema(description = "경매의 종료 시간", example = "2023-11-14 13:00:00")
        private String auctionEndTime;

        @Schema(description = "요청 내용 첨부 이미지", example = "[image1.jpg, image2.jpg]")
        private List<MultipartFile> files; // 클라이언트로부터 업로드할 파일들을 받습니다.
    }

    @Getter
    @Setter
    public static class CreateFormResponse {
        private String message;
        private Long requestId; //tasks table primary key
        private String taskStatus; // tasks table field: status

        public CreateFormResponse(String message, Task task) {
            this.message = message;
            this.requestId = task.getId();
            this.taskStatus = task.getStatus();
        }
    }

}
