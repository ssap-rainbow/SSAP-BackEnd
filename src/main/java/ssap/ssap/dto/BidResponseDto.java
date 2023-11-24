package ssap.ssap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BidResponseDto {
    private Long id;
    private Integer amount;
    private String userEmail;
    private String userName;
    private LocalDateTime time;


}
