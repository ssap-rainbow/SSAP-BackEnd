package ssap.ssap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthDTO {
    private String provider;
    private String providerId;
    private String userName;
    private String userEmail;
    private String accessToken;
    private String refreshToken;
    private String gender;
    private String birthdate;
    private String ageRange;
}