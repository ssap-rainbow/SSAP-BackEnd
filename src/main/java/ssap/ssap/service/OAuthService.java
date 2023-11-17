package ssap.ssap.service;

import jakarta.servlet.http.HttpServletResponse;
import ssap.ssap.dto.LoginResponseDto;

public interface OAuthService {
    LoginResponseDto kakaoLogin(String provider, String code, HttpServletResponse response);

    boolean isAccessTokenValid(String accessToken);

    String refreshAccessToken(String refreshToken);

    void logout(String provider, String accessToken);
}
