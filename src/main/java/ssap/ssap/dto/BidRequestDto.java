package ssap.ssap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequestDto {
    private Long taskId;
    private Long userId;
    private Integer bidAmount;
    private boolean termsAgreed;
    private Long auctionId;


}
