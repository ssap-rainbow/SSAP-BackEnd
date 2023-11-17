package ssap.ssap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    public boolean loginSuccess;
    public Account account;
    public String accessToken;
}
