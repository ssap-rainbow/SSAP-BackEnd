package ssap.ssap.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrandDTO {
    private Long taskId;
    private String title;
    private String description;
    private String auctionStartTime;
    private String auctionEndTime;
    private String thumbnailUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // LocalDateTime을 받아 String으로 변환하여 저장
    public void setCreationTime(LocalDateTime auctionStartTime) {
        this.auctionStartTime = auctionStartTime != null ? auctionStartTime.format(formatter) : null;
    }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) {
        this.auctionEndTime = auctionEndTime != null ? auctionEndTime.format(formatter) : null;
    }
}
