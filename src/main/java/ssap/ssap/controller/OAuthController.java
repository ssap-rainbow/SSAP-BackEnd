package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssap.ssap.dto.LoginResponseDto;
import ssap.ssap.service.OAuthService;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@Tag(name = "OAuth 인증", description = "OAuth2 인증 및 권한 부여 과정을 위한 API EndPoint 입니다.")
public class OAuthController {
    private final OAuthService oauthService;

    @GetMapping("/{provider}/callback")
    public ResponseEntity<LoginResponseDto> callback(
            @Parameter(name = "provider", description = "OAuth 제공자", in = ParameterIn.PATH, required = true)
            @PathVariable String provider,

            @Parameter(name = "code", description = "인증 코드", in = ParameterIn.QUERY, required = true)
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        LoginResponseDto oauthInfo = oauthService.kakaoLogin(provider, code, response);

        return ResponseEntity.ok(oauthInfo);
    }

    @Operation(summary = "액세스 토큰 유효성 검증", description = "제공된 액세스 토큰이 유효한지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰이 유효합니다."),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 유효하지 않거나 만료되었습니다.")
    })
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateAccessToken(
            @Parameter(description = "유효성을 검증할 액세스 토큰", required = true)
            @RequestParam("accessToken") String accessToken
    ) {
        boolean isValid = oauthService.isAccessTokenValid(accessToken);
        if (isValid) {
            return ResponseEntity.status(HttpStatus.OK).body("액세스 토큰이 유효합니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰이 유효하지 않거나 만료되었습니다.");
        }
    }

    @Operation(summary = "액세스 토큰 갱신", description = "제공된 리프레시 토큰을 사용해 액세스 토큰을 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새 액세스 토큰이 생성되었습니다."),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 갱신에 실패했습니다.")
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(
            @Parameter(description = "새 액세스 토큰을 생성하기 위해 사용될 리프레시 토큰", required = true)
            @RequestParam("refreshToken") String refreshToken
    ) {
        String newAccessToken = oauthService.refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("액세스 토큰 갱신에 실패했습니다.");
        }
    }
    @Operation(summary = "OAuth 로그아웃", description = "사용자를 OAuth 세션에서 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth 로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 토큰 만료"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{provider}/logout")
    public ResponseEntity<?> logout(
            @Parameter(name = "provider", description = "OAuth 제공자", required = true) @PathVariable String provider,
            @Parameter(description = "로그아웃할 사용자의 액세스 토큰", required = true) @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.substring("Bearer ".length());
        oauthService.logout(provider, accessToken);
        return ResponseEntity.ok(provider + " 로그아웃 성공");
    }
}
