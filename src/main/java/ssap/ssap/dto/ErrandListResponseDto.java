package ssap.ssap.dto;

import lombok.Data;

@Data
public class ErrandListResponseDto {
    private Long taskId;

    private String title;

    private String description;

    private String district;

    private String startTime;

    private String auctionEndTime;

    private String fee;

    private String fileData;

    // User 관련 필드
    private Long userId;
    private String userName;

    // Category 관련 필드
    private Long categoryId;
    private String categoryName;

    // Auction 관련 필드
    private Long auctionId;
}
