package ssap.ssap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequestDto {
    private Long taskId;
    private String userEmail;
    private Integer bidAmount;
    private boolean termsAgreed;
    private Long auctionId;


}
