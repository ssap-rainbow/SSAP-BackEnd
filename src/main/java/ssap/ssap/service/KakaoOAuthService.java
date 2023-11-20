package ssap.ssap.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ssap.ssap.domain.User;
import ssap.ssap.dto.Account;
import ssap.ssap.dto.LoginResponseDto;
import ssap.ssap.dto.OAuthDTO;
import org.json.JSONObject;
import ssap.ssap.exception.CustomDuplicateKeyException;
import ssap.ssap.exception.CustomServiceException;
import ssap.ssap.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class KakaoOAuthService implements OAuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${KAKAO_CLIENT_ID:default_client_id}")
    private String clientId;

    @Value("${KAKAO_CLIENT_SECRET:default_client_secret}")
    private String clientSecret;

    @Value("${KAKAO_REDIRECT_URL:default_redirect_url}")
    private String redirectUri;

    @Value("${kakao.token-uri:https://kauth.kakao.com/oauth/token}")
    private String kakaoTokenUri;

    @Value("${kakao.user-info-uri:https://kapi.kakao.com/v2/user/me}")
    private String kakaoUserInfoUri;

    @Value("${kakao.token-info-uri:https://kapi.kakao.com/v1/user/access_token_info}")
    private String kakaoTokenInfoUri;

    @Value("${kakao.api.base-url:https://kapi.kakao.com}")
    private String kakaoApiBaseUrl;


    @Autowired
    public KakaoOAuthService(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponseDto kakaoLogin(String provider, String code, HttpServletResponse response) {
        OAuthDTO oauthInfo = requestAccessToken(code);

        try {
            OAuthDTO userInfo = fetchUserInfo(oauthInfo.getAccessToken());

            LoginResponseDto loginResponse = new LoginResponseDto();
            // 로그인 성공 여부 판단 로직
            boolean loginSuccess = isLoginSuccessful(userInfo);

            if (loginSuccess) {
                // 사용자 정보 저장
                User user = saveOrUpdateUser(userInfo);

                Account account = new Account();
                account.setUserName(user.getName());
                account.setUserEmail(user.getEmail());
                account.setGender(user.getGender());
                account.setBirthdate(user.getBirthdate());
                account.setAgeRange(user.getAgeRange());
                account.setProfileImageUrl(user.getProfileImageUrl());
                loginResponse.setAccount(account);
                loginResponse.setLoginSuccess(true);

                log.info("사용자 {}가 성공적으로 로그인했습니다.", user.getName());

                loginResponse.setAccessToken(oauthInfo.getAccessToken());

                Cookie refreshTokenCookie = new Cookie("refreshToken", oauthInfo.getRefreshToken());
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setPath("/");
                response.addCookie(refreshTokenCookie);

                // 토큰 정보를 LoginResponseDto에 설정
                loginResponse.setAccessToken(oauthInfo.getAccessToken());

                return loginResponse;
            } else {
                loginResponse.setLoginSuccess(false);

                log.error("로그인에 실패했습니다.");

                return loginResponse;
            }
        } catch (Exception e) {
            log.error("로그인 중 오류 발생: {}", e.getMessage());

            LoginResponseDto errorResponse = new LoginResponseDto();
            errorResponse.setLoginSuccess(false);

            return errorResponse;
        }
    }

    private User mapOAuthDTOToUserEntity(OAuthDTO oauthInfo) {
        User user = new User();
        user.setProviderId(oauthInfo.getProviderId());
        user.setName(oauthInfo.getUserName());
        user.setEmail(oauthInfo.getUserEmail());
        user.setGender(oauthInfo.getGender());
        user.setBirthdate(oauthInfo.getBirthdate());
        user.setAgeRange(oauthInfo.getAgeRange());
        user.setProfileImageUrl(oauthInfo.getProfileImageUrl());


        return user;
    }

    private boolean isLoginSuccessful(OAuthDTO userInfo) {
        return userInfo != null &&
                !userInfo.getUserName().isEmpty() &&
                !userInfo.getUserEmail().isEmpty();
    }

    @Transactional
    public User saveOrUpdateUser(OAuthDTO oauthInfo) {
        // 사용자가 데이터베이스에 이미 있는지 확인합니다.
        Optional<User> existingUserOpt = userRepository.findByProviderId(oauthInfo.getProviderId());

        try {
            if (existingUserOpt.isPresent()) {
                // 기존 사용자가 이미 있으므로 아무런 업데이트도 하지 않고 반환합니다.
                return existingUserOpt.get();
            } else {
                User newUser = mapOAuthDTOToUserEntity(oauthInfo);

                return userRepository.save(newUser);
            }
        } catch (DataIntegrityViolationException e) {
            // 데이터베이스 레벨의 유니크 제약 조건 위반이 발생하면 CustomDuplicateKeyException을 던집니다.
            throw new CustomDuplicateKeyException("Provider ID " + oauthInfo.getProviderId() + " already exists.");
        }
    }

    private OAuthDTO requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoTokenUri,
                HttpMethod.POST,
                request,
                String.class
        );

        JSONObject jsonObj = new JSONObject(response.getBody());

        OAuthDTO oauthInfo = new OAuthDTO();
        oauthInfo.setAccessToken(jsonObj.getString("access_token"));
        oauthInfo.setRefreshToken(jsonObj.optString("refresh_token"));

        return oauthInfo;
    }

    private OAuthDTO fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoUserInfoUri,
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        JSONObject jsonObj = new JSONObject(response.getBody());
        JSONObject account = jsonObj.getJSONObject("kakao_account");

        String gender = account.optString("gender");
        String birthdate = account.optString("birthday");
        String ageRange = account.optString("age_range");
        String profileImageUrl = account.optJSONObject("profile").optString("thumbnail_image_url");


        OAuthDTO oauthInfo = new OAuthDTO();
        oauthInfo.setProvider("kakao");
        oauthInfo.setProviderId(String.valueOf(jsonObj.getLong("id")));
        oauthInfo.setUserName(account.optJSONObject("profile").optString("nickname"));
        oauthInfo.setUserEmail(account.optString("email"));
        oauthInfo.setAccessToken(accessToken);
        oauthInfo.setGender(gender);
        oauthInfo.setBirthdate(birthdate);
        oauthInfo.setAgeRange(ageRange);
        oauthInfo.setProfileImageUrl(profileImageUrl);

        return oauthInfo;
    }

    public boolean isAccessTokenValid(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(kakaoTokenInfoUri, HttpMethod.GET, httpEntity, String.class);
            return true; // 토큰이 유효한 경우
        } catch (HttpClientErrorException e) {
            log.error("액세스 토큰 유효성 검사 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않거나 만료되었습니다.", e);
        }
    }

    public String refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(kakaoTokenUri, HttpMethod.POST, request, String.class);
            JSONObject jsonObj = new JSONObject(response.getBody());
            return jsonObj.getString("access_token"); // 새로운 액세스 토큰 반환
        } catch (HttpClientErrorException e) {
            log.error("액세스 토큰 갱신 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "액세스 토큰 갱신에 실패했습니다.", e);
        }
    }

    @Override
    public void logout(String provider, String accessToken) {
        if (!"kakao".equalsIgnoreCase(provider)) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        String logoutUri = kakaoApiBaseUrl + "/v1/user/logout";

        try {
            restTemplate.exchange(logoutUri, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            throw new CustomServiceException("카카오 로그아웃 중 오류가 발생했습니다.", e);
        }
    }

}