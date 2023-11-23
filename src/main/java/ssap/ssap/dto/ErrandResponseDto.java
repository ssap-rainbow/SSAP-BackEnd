package ssap.ssap.dto;

import lombok.Data;

@Data
public class ErrandResponseDto {
    private Long taskId;

    private String title;

    private String description;

    private String district;

    private String startTime;

    private String auctionEndTime;

    private String fee;

    private String fileData;
}
